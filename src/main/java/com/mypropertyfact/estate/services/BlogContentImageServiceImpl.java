package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.entities.BlogContentImage;
import com.mypropertyfact.estate.interfaces.BlogContentImageService;
import com.mypropertyfact.estate.repositories.BlogContentImageRepository;
import com.mypropertyfact.estate.models.InvalidRequestException;
import com.mypropertyfact.estate.models.ResourceNotFoundException;
import com.mypropertyfact.estate.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BlogContentImageServiceImpl implements BlogContentImageService {

    @Autowired
    private BlogContentImageRepository blogContentImageRepository;

    @Autowired
    private FileUtils fileUtils;

    @Value("${upload_dir}")
    private String dir;

    @Override
    public Response addUpdateContentImage(MultipartFile file, BlogContentImage blogContentImage) {
        if (file == null) {
            throw new InvalidRequestException("Image file is required!");
        }
        if (blogContentImage == null) {
            throw new InvalidRequestException("Blog content image data is required!");
        }
        if (!fileUtils.isTypeImage(file)) {
            throw new IllegalArgumentException("File type should be an image!");
        }

        if (blogContentImage.getImageWidth() <= 0 || blogContentImage.getImageHeight() <= 0) {
            throw new InvalidRequestException("Image dimensions must be greater than zero!");
        }

        String uploadDir = Paths.get(dir, "blog/content-image").toString();
        String imageName = fileUtils.renameFile(file, UUID.randomUUID().toString());

        String savedImagePath = fileUtils.saveFile(
                file,
                imageName,
                uploadDir,
                blogContentImage.getImageWidth(),
                blogContentImage.getImageHeight(),
                1.0f
        );

        if (blogContentImage.getId() > 0) {
            BlogContentImage existingImage = blogContentImageRepository.findById(blogContentImage.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Blog content image not found for update."));

            // Delete old image if exists
            if (existingImage.getImage() != null) {
                fileUtils.deleteFileFromDestination(uploadDir, existingImage.getImage());
            }

            existingImage.setImage(savedImagePath);
            existingImage.setImageWidth(blogContentImage.getImageWidth());
            existingImage.setImageHeight(blogContentImage.getImageHeight());

            blogContentImageRepository.save(existingImage);
            return new Response(1, "Image updated successfully.");
        } else {
            blogContentImage.setImage(savedImagePath);
            blogContentImageRepository.save(blogContentImage);
            return new Response(1, "Image saved successfully.");
        }
    }

    @Override
    public List<BlogContentImage> getAllImageList() {
        return blogContentImageRepository.findAll();
    }

    @Override
    public Response deleteImage(int id) {
        return null;
    }
}
