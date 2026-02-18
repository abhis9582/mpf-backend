package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.entities.*;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class ProjectExcelUploadService {

    @Value("${upload_dir}")
    private String uploadDir;

    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BuilderRepository builderRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ProjectStatusRepository projectStatusRepository;
    @Autowired
    private ProjectTypeRepository projectTypeRepository;
    @Autowired
    private ProjectGalleryRepository projectGalleryRepository;
    @Autowired
    private ProjectAboutRepository projectAboutRepository;

    @Autowired
    private ProjectDesktopBannerRepository projectDesktopBannerRepository;

    @Autowired
    private ProjectMobileBannerRepository projectMobileBannerRepository;

    @Autowired
    private ProjectWalkthroughRepository projectWalkthroughRepository;

    /* Higher-resolution targets so saved images stay sharp (frontend can scale down for display). */
    private static final int THUMBNAIL_W = 1200;
    private static final int THUMBNAIL_H = 1200;
    private static final int LOCATION_MAP_W = 1630;
    private static final int LOCATION_MAP_H = 1626;
    private static final int GALLERY_W = 1600;
    private static final int GALLERY_H = 1200;
    private static final int DESKTOP_H = 1200;
    private static final int DESKTOP_W = 2508;
    private static final int MOBILE_W = 800;
    private static final int MOBILE_H = 800;

    @Transactional(rollbackFor = Exception.class)
    public Response uploadProjectsExcel(MultipartFile excelFile, MultipartFile imagesZip) {
        Response response = new Response();
        if (excelFile == null || excelFile.isEmpty()) {
            response.setIsSuccess(0);
            response.setMessage("Excel file is required");
            return response;
        }
        String filename = excelFile.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            response.setIsSuccess(0);
            response.setMessage("Only Excel files (.xlsx or .xls) are allowed");
            return response;
        }

        Map<String, byte[]> imageMap = new HashMap<>();
        if (imagesZip != null && !imagesZip.isEmpty() && imagesZip.getOriginalFilename() != null
                && imagesZip.getOriginalFilename().toLowerCase().endsWith(".zip")) {
            try {
                extractZipToMap(imagesZip.getInputStream(), imageMap);
            } catch (Exception e) {
                log.warn("Could not extract images zip: {}", e.getMessage());
            }
        }

        List<String> errors = new ArrayList<>();
        int created = 0;
        int updated = 0;

        try (InputStream is = excelFile.getInputStream();
                Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() < 2) {
                response.setIsSuccess(0);
                response.setMessage("Excel must have a header row and at least one data row");
                return response;
            }

            // Extract embedded images from Excel (key = "row_col" 0-based)
            Map<String, byte[]> embeddedImages = extractEmbeddedImages(sheet);

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colIndex = buildHeaderIndex(headerRow);

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null)
                    continue;

                try {
                    String projectName = getCell(colIndex, row, "PROJECT NAME");
                    if (projectName == null || projectName.trim().isEmpty()) {
                        errors.add("Row " + (r + 1) + ": Project name is required");
                        continue;
                    }

                    Project project = resolveOrCreateProject(row, colIndex, projectName.trim(), errors, r + 1);
                    if (project == null)
                        continue;

                    String slug = project.getSlugURL();
                    if (slug == null || slug.isEmpty()) {
                        slug = fileUtils.generateSlug(projectName);
                        project.setSlugURL(slug);
                    }
                    String projectDir = uploadDir + "properties/" + slug + "/";
                    int dataRowIndex = r; // sheet row index (row 0 = header, row 1 = first data row)

                    // Image columns: prefer embedded image in cell, else file name in zip
                    saveImageFromEmbeddedOrZip(embeddedImages, imageMap, project, row, dataRowIndex, colIndex,
                            projectDir,
                            "PROJECT LOGO IMAGE", (p, name) -> p.setProjectLogo(name), 200, 50);

                    boolean isNew = project.getId() == 0;
                    projectRepository.save(project);
                    for (int g = 1; g <= 16; g++) {
                        String colName = "Desktop image " + g;
                        Integer colIdx = colIndex.get(colName.toUpperCase().replaceAll("\\s+", " "));
                        byte[] imgData = null;
                        String suggestedName = "desktop" + g;
                        if (colIdx != null) {
                            String embeddedKey = dataRowIndex + "_" + colIdx;
                            imgData = embeddedImages.get(embeddedKey);
                        }
                        if (imgData == null) {
                            String fileName = getCell(colIndex, row, colName);
                            if (fileName != null && !fileName.trim().isEmpty()) {
                                imgData = findImageInMap(imageMap, fileName.trim());
                                suggestedName = fileName.trim();
                            }
                        }
                        if (imgData != null) {
                            String saved = fileUtils.saveImageFromBytes(imgData, suggestedName, projectDir, DESKTOP_W,
                                    DESKTOP_H);
                            if (saved != null) {
                                ProjectDesktopBanner projectDesktopBanner = new ProjectDesktopBanner();
                                projectDesktopBanner.setProject(project);
                                projectDesktopBanner.setDesktopImage(saved);
                                projectDesktopBanner.setDesktopAltTag(suggestedName);
                                projectDesktopBannerRepository.save(projectDesktopBanner);
                            }
                        }
                    }

                    for (int g = 1; g <= 16; g++) {
                        String colName = "Mobile image " + g;
                        Integer colIdx = colIndex.get(colName.toUpperCase().replaceAll("\\s+", " "));
                        byte[] imgData = null;
                        String suggestedName = "mobile" + g;
                        if (colIdx != null) {
                            String embeddedKey = dataRowIndex + "_" + colIdx;
                            imgData = embeddedImages.get(embeddedKey);
                        }
                        if (imgData == null) {
                            String fileName = getCell(colIndex, row, colName);
                            if (fileName != null && !fileName.trim().isEmpty()) {
                                imgData = findImageInMap(imageMap, fileName.trim());
                                suggestedName = fileName.trim();
                            }
                        }
                        if (imgData != null) {
                            String saved = fileUtils.saveImageFromBytes(imgData, suggestedName, projectDir, MOBILE_W,
                                    MOBILE_H);
                            if (saved != null) {
                                ProjectMobileBanner projectMobileBanner = new ProjectMobileBanner();
                                projectMobileBanner.setProject(project);
                                projectMobileBanner.setMobileImage(saved);
                                projectMobileBanner.setMobileAltTag(suggestedName);
                                projectMobileBannerRepository.save(projectMobileBanner);
                            }
                        }
                    }

                    Optional<ProjectWalkthrough> exestigProjectWalkthrough = projectWalkthroughRepository.findByProject(project);
                    if(exestigProjectWalkthrough.isEmpty()) {
                        if (project.getId() > 0 && getCell(colIndex, row, "PROJECT WALKTHROUGH DESCRIPTION") != null && !getCell(colIndex, row, "PROJECT WALKTHROUGH DESCRIPTION").trim().isEmpty()) {
                            ProjectWalkthrough projectWalkthrough = new ProjectWalkthrough();
                            projectWalkthrough.setWalkthroughDesc(getCell(colIndex, row, "PROJECT WALKTHROUGH DESCRIPTION"));
                            projectWalkthrough.setProject(project);
                            projectWalkthroughRepository.save(projectWalkthrough);
                        }
                    }
                    for (int g = 1; g <= 16; g++) {
                        String colName = "GALLERY IMAGE " + g;
                        Integer colIdx = colIndex.get(colName.toUpperCase().replaceAll("\\s+", " "));
                        byte[] imgData = null;
                        String suggestedName = "gallery" + g;
                        if (colIdx != null) {
                            String embeddedKey = dataRowIndex + "_" + colIdx;
                            imgData = embeddedImages.get(embeddedKey);
                        }
                        if (imgData == null) {
                            String fileName = getCell(colIndex, row, colName);
                            if (fileName != null && !fileName.trim().isEmpty()) {
                                imgData = findImageInMap(imageMap, fileName.trim());
                                suggestedName = fileName.trim();
                            }
                        }
                        if (imgData != null) {
                            String saved = fileUtils.saveImageFromBytes(imgData, suggestedName, projectDir, GALLERY_W,
                                    GALLERY_H);
                            if (saved != null) {
                                ProjectGallery gallery = new ProjectGallery();
                                gallery.setProject(project);
                                gallery.setImage(saved);
                                gallery.setSlugUrl(slug);
                                gallery.setType("gallery");
                                projectGalleryRepository.save(gallery);
                            }
                        }
                    }

                    String aboutDesc = getCell(colIndex, row, "ABOUT PROJECT DESCRIPTION");
                    if (aboutDesc != null && !aboutDesc.trim().isEmpty()) {
                        ProjectsAbout about = projectAboutRepository.findByProject_Id(project.getId()).orElse(null);
                        if (about == null) {
                            about = new ProjectsAbout();
                            about.setProject(project);
                        }
                        about.setLongDesc(aboutDesc.trim());
                        projectAboutRepository.save(about);
                    }

                    if (isNew)
                        created++;
                    else
                        updated++;

                } catch (Exception e) {
                    errors.add("Row " + (r + 1) + ": " + e.getMessage());
                    log.warn("Row {} error: {}", r + 1, e.getMessage());
                }
            }

            StringBuilder msg = new StringBuilder();
            msg.append("Upload completed. Created: ").append(created).append(", Updated: ").append(updated);
            if (!errors.isEmpty()) {
                msg.append(". Errors: ").append(errors.size()).append(" - ")
                        .append(String.join("; ", errors.subList(0, Math.min(10, errors.size()))));
                if (errors.size() > 10)
                    msg.append("...");
            }
            response.setIsSuccess(1);
            response.setMessage(msg.toString());

        } catch (Exception e) {
            log.error("Excel upload failed", e);
            response.setIsSuccess(0);
            response.setMessage("Failed to process Excel: " + e.getMessage());
        }

        return response;
    }

    private Map<String, Integer> buildHeaderIndex(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        if (headerRow == null)
            return map;
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell c = headerRow.getCell(i);
            String val = getCellValueAsString(c);
            if (val != null && !val.trim().isEmpty()) {
                map.put(val.trim().toUpperCase().replaceAll("\\s+", " "), i);
            }
        }
        return map;
    }

    private String getCell(Map<String, Integer> colIndex, Row row, String headerName) {
        Integer idx = colIndex.get(headerName.toUpperCase().replaceAll("\\s+", " "));
        if (idx == null)
            return null;
        Cell c = row.getCell(idx.intValue());
        return getCellValueAsString(c);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double n = cell.getNumericCellValue();
                if (n == Math.floor(n))
                    return String.valueOf((long) n);
                return String.valueOf(n);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            default:
                return null;
        }
    }

    private Project resolveOrCreateProject(Row row, Map<String, Integer> colIndex, String projectName,
            List<String> errors, int rowNum) {
        String builderName = getCell(colIndex, row, "BUILDER NAME");
        String cityName = getCell(colIndex, row, "CITY NAME");
        String stateName = getCell(colIndex, row, "STATE NAME");
        String countryName = getCell(colIndex, row, "COUNTRY NAME");
        String projectStatusName = getCell(colIndex, row, "PROJECT STATUS NAME");
        String propertyTypeName = getCell(colIndex, row, "PROPERTY TYPE");

        if (builderName == null || builderName.trim().isEmpty()) {
            errors.add("Row " + rowNum + ": Builder name is required");
            return null;
        }
        if (cityName == null || cityName.trim().isEmpty()) {
            errors.add("Row " + rowNum + ": City name is required");
            return null;
        }

        Builder builder = findOrCreateBuilder(builderName.trim());
        Country country = findOrCreateCountry(countryName != null ? countryName.trim() : "India");
        State state = findOrCreateState(stateName != null ? stateName.trim() : "N/A", country);
        City city = findOrCreateCity(cityName.trim(), state);
        String statusName = (projectStatusName != null && !projectStatusName.trim().isEmpty())
                ? projectStatusName.trim()
                : "Under Construction";
        ProjectStatus status = projectStatusRepository.findByStatusNameIgnoreCase(statusName)
                .orElseGet(() -> projectStatusRepository.findAll().isEmpty() ? null
                        : projectStatusRepository.findAll().get(0));
        ProjectTypes projectType = null;
        if (propertyTypeName != null && !propertyTypeName.trim().isEmpty()) {
            projectType = projectTypeRepository.findByProjectTypeNameIgnoreCase(propertyTypeName.trim())
                    .orElse(null);
        }
        if (projectType == null && !projectTypeRepository.findAll().isEmpty()) {
            projectType = projectTypeRepository.findAll().get(0);
        }

        Optional<Project> existing = projectRepository.findBySlugURL(fileUtils.generateSlug(projectName));
        if (existing.isPresent()) {
            Project p = existing.get();
            mapRowToProject(p, row, colIndex, builder, city, status, projectType);
            return p;
        }

        Project project = new Project();
        project.setProjectName(projectName);
        project.setSlugURL(fileUtils.generateSlug(projectName));
        project.setStatus(true);
        project.setShowFeaturedProperties(false);
        mapRowToProject(project, row, colIndex, builder, city, status, projectType);
        return project;
    }

    private void mapRowToProject(Project project, Row row, Map<String, Integer> colIndex,
            Builder builder, City city, ProjectStatus status, ProjectTypes projectType) {
        project.setBuilder(builder);
        project.setCity(city);
        if (status != null)
            project.setProjectStatus(status);
        if (projectType != null)
            project.setProjectTypes(projectType);

        setIfPresent(project::setProjectLocality, getCell(colIndex, row, "PROJECT LOCALITY"));
        setIfPresent(project::setProjectConfiguration, getCell(colIndex, row, "PROJECT CONFIGURATION"));
        setIfPresent(project::setProjectPrice, getCell(colIndex, row, "PROJECT PRICE"));
        setIfPresent(project::setReraNo, getCell(colIndex, row, "RERA NO"));
        setIfPresent(project::setFloorPlanDesc, getCell(colIndex, row, "FLOOR PLAN DESCRIPTION"));
        setIfPresent(project::setLocationDesc, getCell(colIndex, row, "LOCATION DESCRIPTION"));
        setIfPresent(project::setAmenityDesc, getCell(colIndex, row, "AMENITY DESCRIPTION"));
        setIfPresent(project::setMetaTitle, getCell(colIndex, row, "META TITLE"));
        setIfPresent(project::setMetaDescription, getCell(colIndex, row, "META DESCRIPTION"));
        setIfPresent(project::setMetaKeyword, getCell(colIndex, row, "META KEYWORD"));
        setIfPresent(project::setIvrNo, getCell(colIndex, row, "IVR NO"));
        setIfPresent(project::setReraWebsite, getCell(colIndex, row, "RERA WEBSITE"));
    }

    private static void setIfPresent(java.util.function.Consumer<String> setter, String value) {
        if (value != null && !value.trim().isEmpty())
            setter.accept(value.trim());
    }

    private Builder findOrCreateBuilder(String name) {
        return builderRepository.findByBuilderNameIgnoreCase(name)
                .orElseGet(() -> {
                    Builder b = new Builder();
                    b.setBuilderName(name);
                    b.setSlugUrl(fileUtils.generateSlug(name));
                    return builderRepository.save(b);
                });
    }

    private Country findOrCreateCountry(String name) {
        return countryRepository.findByCountryNameIgnoreCase(name)
                .orElseGet(() -> {
                    Country c = new Country();
                    c.setCountryName(name);
                    return countryRepository.save(c);
                });
    }

    private State findOrCreateState(String name, Country country) {
        Optional<State> opt = stateRepository.findByStateName(name);
        if (opt.isPresent())
            return opt.get();
        State s = new State();
        s.setStateName(name);
        s.setCountry(country);
        return stateRepository.save(s);
    }

    private City findOrCreateCity(String name, State state) {
        Optional<City> opt = cityRepository.findByNameIgnoreCase(name);
        if (opt.isPresent())
            return opt.get();
        City c = new City();
        c.setName(name);
        c.setSlugUrl(fileUtils.generateSlug(name));
        c.setState(state);
        return cityRepository.save(c);
    }

    private void extractZipToMap(InputStream zipIs, Map<String, byte[]> out) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(zipIs)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory())
                    continue;
                String name = entry.getName();
                if (name.contains("/"))
                    name = name.substring(name.lastIndexOf('/') + 1);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                int n;
                while ((n = zis.read(buf)) > 0)
                    baos.write(buf, 0, n);
                out.put(name, baos.toByteArray());
                out.put(name.toLowerCase(), baos.toByteArray());
            }
        }
    }

    private byte[] findImageInMap(Map<String, byte[]> imageMap, String fileName) {
        if (imageMap.containsKey(fileName))
            return imageMap.get(fileName);
        if (imageMap.containsKey(fileName.toLowerCase()))
            return imageMap.get(fileName.toLowerCase());
        return imageMap.get(fileName);
    }

    /**
     * Read raw bytes from an OOXML package part (e.g. xl/media/image1.png).
     * This is the full-resolution image as stored in the file, not the display size
     * used when the image is fitted in the column.
     */
    private static byte[] readPartBytes(PackagePart part) throws IOException {
        try (InputStream is = part.getInputStream(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) != -1) {
                os.write(buf, 0, n);
            }
            return os.toByteArray();
        }
    }

    /**
     * Extract embedded images from the Excel sheet at their <b>real size and quality</b>
     * (raw bytes from xl/media/). Images fitted in columns are only display-sized in the
     * sheet; the stored part is read as-is. Key = "row_col" (0-based row and column).
     * Only works for .xlsx (XSSF).
     */
    private Map<String, byte[]> extractEmbeddedImages(Sheet sheet) {
        Map<String, byte[]> out = new HashMap<>();
        if (!(sheet instanceof XSSFSheet xssfSheet)) {
            return out;
        }
        try {
            org.apache.poi.ss.usermodel.Drawing<?> drawing = xssfSheet.getDrawingPatriarch();
            if (drawing == null || !(drawing instanceof XSSFDrawing xssfDrawing)) {
                return out;
            }
            for (org.apache.poi.ss.usermodel.Shape shape : xssfDrawing.getShapes()) {
                if (!(shape instanceof XSSFPicture picture)) {
                    continue;
                }
                org.apache.poi.ss.usermodel.ClientAnchor anchor = picture.getClientAnchor();
                if (anchor == null)
                    continue;
                int row = anchor.getRow1();
                int col = anchor.getCol1();
                PictureData pictData = picture.getPictureData();
                if (pictData == null)
                    continue;
                byte[] data = null;
                if (pictData instanceof XSSFPictureData xssfPic) {
                    PackagePart part = xssfPic.getPackagePart();
                    if (part != null) {
                        data = readPartBytes(part);
                    }
                }
                if (data == null || data.length == 0) {
                    data = pictData.getData();
                }
                if (data == null || data.length == 0)
                    continue;
                String key = row + "_" + col;
                out.put(key, data);
            }
        } catch (Exception e) {
            log.warn("Could not extract embedded images from sheet: {}", e.getMessage());
        }
        return out;
    }

    /**
     * Save image for a column: first try embedded image in that cell (row, col),
     * then try zip by cell filename.
     */
    private void saveImageFromEmbeddedOrZip(Map<String, byte[]> embeddedImages, Map<String, byte[]> imageMap,
            Project project, Row row, int dataRowIndex, Map<String, Integer> colIndex,
            String projectDir, String columnName,
            java.util.function.BiConsumer<Project, String> setter, int w, int h) {
        Integer colIdx = colIndex.get(columnName.toUpperCase().replaceAll("\\s+", " "));
        byte[] data = null;
        String suggestedName = columnName.replaceAll("\\s+", "_").toLowerCase();
        if (colIdx != null) {
            String embeddedKey = dataRowIndex + "_" + colIdx;
            data = embeddedImages.get(embeddedKey);
        }
        if (data == null) {
            // Fallback: cell value = file name in zip
            // Ozar-96_Desktop Banner_My-Property-Fact
            Set<String> imageNames = imageMap.keySet().stream().filter(image-> image.contains(project.getProjectName().replace(" ", "-"))).collect(Collectors.toSet());
            for (String name: imageNames) {
                String[] s = name.split("_");
                if(s.length > 1) {
                    String projectName = s[0].replace("-", " ");
                    String imageType = s[1];
                    saveImageFromMap(imageMap, project, row, colIndex, projectDir, name, setter, w, h,
                            projectName, imageType);
                }
            }
            return;
        }
        String saved = fileUtils.saveImageFromBytes(data, suggestedName, projectDir, w, h);
        if (saved != null)
            setter.accept(project, saved);
    }

    private void saveImageFromMap(Map<String, byte[]> imageMap, Project project, Row row,
            Map<String, Integer> colIndex, String projectDir, String columnName,
            java.util.function.BiConsumer<Project, String> setter, int w, int h,
                                  String projectName, String imageType) {
//        if (row == null)
//            return;
//        if (columnName == null || columnName.trim().isEmpty())
//            return;
//        byte[] data = findImageInMap(imageMap, columnName.trim());
//        if (data == null)
//            return;
        if(imageType.startsWith("Desktop")) {
            String type = imageType.contains("-") ? imageType.replace("-", " ") : imageType;
            String[] s = type.split(" ");
            projectName = projectName.replace(" ", "-") + "_" + imageType;
            int ind = Integer.parseInt(s[s.length - 1]);
            for(int i=0;i<ind-1;i++) {
                byte[] data = findImageInMap(imageMap, projectName);
                String saved = fileUtils.saveImageFromBytes(data, imageType, projectDir, w, h);
                if (saved != null)
                    setter.accept(project, saved);
            }
        }
    }
}
