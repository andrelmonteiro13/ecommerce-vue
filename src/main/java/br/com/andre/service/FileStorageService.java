package br.com.andre.service;

import br.com.andre.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.util.UUID;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FileStorageService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public FileStorageService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public Mono<String> uploadProductImage(FilePart file) {
        return DataBufferUtils.join(file.content()).flatMap(dataBuffer -> {
            try {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);

                String filename = file.filename();
                String extension = getExtension(filename);
                String objectName = UUID.randomUUID() + extension;
                String contentType =
                    file.headers().getContentType() != null ? file.headers().getContentType().toString() : "application/octet-stream";

                boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucket()).build());

                if (!bucketExists) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
                }

                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(properties.getBucket())
                        .object(objectName)
                        .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                        .contentType(contentType)
                        .build()
                );

                return Mono.just(properties.getPublicUrl() + "/" + properties.getBucket() + "/" + objectName);
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Erro ao enviar imagem para o MinIO.", e));
            }
        });
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
