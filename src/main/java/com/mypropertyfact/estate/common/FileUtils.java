package com.mypropertyfact.estate.common;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.UUID;

@Component
public class FileUtils {

    //checking size of file
    public boolean isFileSizeValid(MultipartFile file, long maxSizeInBytes) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null!");
        }
        return file.getSize() <= maxSizeInBytes;
    }

    //Check type of file
    public boolean isTypeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty!");
        }
        String contentType = file.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("image/");
    }
    //Renaming the name of file
    public String renameFile(MultipartFile file, String suffix) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.lastIndexOf('.') == -1) {
            // If no extension, just return a UUID as the filename
            return suffix;
        }
        String newFileName = suffix;
        return newFileName;
    }

    //generating slug url
    public String generateSlug(String input) {
        if (input == null) return "";
        return input.trim()
                .replaceAll("[^a-zA-Z0-9\\s]", "") // remove special chars
                .replaceAll("\\s+", "-")           // replace spaces with hyphens
                .toLowerCase();
    }

    // Saving file to destination
    public String saveFile(MultipartFile file, String newFileNameWithoutExtension, String uploadDir, int width, int height, float webpQuality) {
        String imageName =null;
        try {
            Path uploadPath = Paths.get(uploadDir+"blog/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Temporary file to store the uploaded image
            File tempOriginalFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
            file.transferTo(tempOriginalFile);

            // Resize the image and save to a temp resized file (use JPEG/PNG format)
            File tempResizedFile = File.createTempFile("resized_", ".jpg"); // You can change format if needed
            resizeImage(tempOriginalFile, tempResizedFile, width, height, "jpg", webpQuality);

            // Final output file as WebP
            String finalFileName = newFileNameWithoutExtension + ".jpeg";
            File finalWebPFile = new File(uploadPath.toFile(), finalFileName);
            convertToJpeg(tempResizedFile, finalWebPFile, webpQuality);

            // Cleanup temporary files
            tempOriginalFile.delete();
            tempResizedFile.delete();
            imageName = finalFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save and convert file", e);
        }
        return imageName;
    }

    //delete file from destination
    public boolean deleteFileFromDestination(String fileName, String dirPath) {
        if (fileName != null && !fileName.isEmpty()) {
            File oldFile = new File(dirPath, fileName);
            if (oldFile.exists()) {
                return oldFile.delete();
            }
        }
        return false;
    }

    public void convertToJpeg(File inputFile, File outputFile, float quality) throws IOException {
        // Read the input image
        BufferedImage image = ImageIO.read(inputFile);
        if (image == null) {
            throw new IOException("Invalid image file: " + inputFile.getName());
        }

        // Get WebP writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No Jpeg writer found. Make sure TwelveMonkeys plugins are loaded.");
        }
        ImageWriter writer = writers.next();

        // Configure compression
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality); // Quality from 0.0 (low) to 1.0 (high)
        }

        // Write the output file
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {

            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    //Resizing the image
    public void resizeImage(File inputFile, File outputFile, int targetWidth, int targetHeight, String formatName, float quality) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputFile);
        if (originalImage == null) {
            throw new IOException("Invalid image file: " + inputFile.getName());
        }

        // Create high-quality scaled image
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        // Write the image with compression
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writer found for format: " + formatName);
        }
        ImageWriter writer = writers.next();

        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile);
             ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
            writer.setOutput(ios);
            writer.write(null, new javax.imageio.IIOImage(resizedImage, null, null), param);
        } finally {
            writer.dispose();
        }
    }


}
