package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.SubmissionCreatedEvent;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.repository.SubmissionRepository;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SubmissionCreatedEventService implements Consumer<SubmissionCreatedEvent> {
  private static final int MAX_DIMENSION = 128;

  private final BucketComponent bucketComponent;
  private final SubmissionRepository repository;

  @SneakyThrows
  @Override
  public void accept(SubmissionCreatedEvent event) {
    File original = bucketComponent.download(event.getOriginalKey());
    BufferedImage image = ImageIO.read(original);
    if (image == null) {
      throw new RuntimeException(
          "Le fichier téléchargé n'est pas une image: " + event.getOriginalKey());
    }

    File thumbnail = writeThumbnail(image, event.getSubmissionId());
    String thumbnailKey = "thumbnails/" + event.getSubmissionId() + ".png";
    bucketComponent.upload(thumbnail, thumbnailKey);

    repository
        .findById(event.getSubmissionId())
        .ifPresent(
            submission -> {
              submission.setThumbnailKey(thumbnailKey);
              repository.save(submission);
            });

    cleanUp(original, thumbnail);
  }

  @SneakyThrows
  private File writeThumbnail(BufferedImage image, String submissionId) {
    BufferedImage scaled = scale(image, MAX_DIMENSION);
    File thumbnail = File.createTempFile("thumbnail-" + submissionId, ".png");
    ImageIO.write(scaled, "png", thumbnail);
    return thumbnail;
  }

  private BufferedImage scale(BufferedImage source, int maxDimension) {
    double ratio = maxDimension / (double) Math.max(source.getWidth(), source.getHeight());
    int width = Math.max(1, (int) (source.getWidth() * ratio));
    int height = Math.max(1, (int) (source.getHeight() * ratio));

    BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = result.createGraphics();
    graphics.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    graphics.drawImage(source, 0, 0, width, height, null);
    graphics.dispose();
    return result;
  }

  private void cleanUp(File... files) {
    for (File file : files) {
      if (file != null) {
        file.delete();
      }
    }
  }
}
