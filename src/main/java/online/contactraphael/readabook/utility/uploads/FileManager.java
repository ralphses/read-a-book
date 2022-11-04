package online.contactraphael.readabook.utility.uploads;

import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.exception.FileStorageException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.response.FileUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.http.MediaType.*;

@Component
@Slf4j
public class FileManager {
    private final FileStorage fileStorage;
    private final Path path;

    public FileManager(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        this.path = Paths.get(fileStorage.directory()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.path);
        } catch (IOException e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    public String saveFile(MultipartFile file, String fileName) {

        boolean isFileValid =
                file.isEmpty() ||
                file.getSize() == 0 ||
                !(Objects.requireNonNull(file.getContentType()).equalsIgnoreCase(APPLICATION_PDF_VALUE));

        if(isFileValid || fileName.contains("..")) {
            throw new FileStorageException("Invalid or empty file");
        }

        try {

            Path newPath = path.resolve(fileName);
            Files.copy(file.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (IOException e) {
            throw new FileStorageException("error uploading file to server");
        }
    }

    public Resource loadFile(String fileName) {
        try {

            Path filePath = path.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) return resource; else throw new ResourceNotFoundException("File not found " + fileName);

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File not found " + fileName);
        }
    }


    public void deleteFile(String fileName) throws IOException {
        Path location = Paths.get(fileStorage.directory()+"/"+fileName);
        Files.deleteIfExists(location);
    }

    public FileUploadResponse getFileUploadResponse(HttpServletRequest httpServletRequest, String documentName) {

        Resource documentResource = loadFile(documentName);

        try {
            AtomicReference<String> contentType = new AtomicReference<>(
                    httpServletRequest.getServletContext().getMimeType(documentResource.getFile().getAbsolutePath()));

            if(contentType.get() == null) contentType.set("application/octet-stream");

            return new FileUploadResponse(
                    documentResource.getFilename(),
                    "",
                    contentType.get(),
                    0,
                    documentResource);

        } catch (IOException e) {
            log.info("Could not determine file type");
            throw new ResourceNotFoundException("Could not determine file type");
        }
    }
}
