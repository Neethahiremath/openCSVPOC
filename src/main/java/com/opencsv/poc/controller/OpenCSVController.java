package com.opencsv.poc.controller;

import com.opencsv.poc.model.Animal;
import com.opencsv.poc.model.OpenCSVConstant;
import com.opencsv.poc.service.OpenCSVService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.opencsv.poc.model.OpenCSVConstant.FILE_TYPE;

@RestController
@Slf4j
public class OpenCSVController {

  private OpenCSVService openCSVService;

  @Autowired
  public OpenCSVController(OpenCSVService openCSVService) {
    this.openCSVService = openCSVService;
  }

  @PostMapping(value = "/processCSV", produces = "text/csv")
  public void processCSVFile(
      @RequestParam("csvfile") final MultipartFile csvFile, HttpServletResponse httpResponse)
      throws IOException {
    long startTime = System.currentTimeMillis();
    String filename =
        OpenCSVConstant.OUTPUT_FILE + System.currentTimeMillis() + OpenCSVConstant.FILE_EXTN;
    if (!FILE_TYPE.equals(csvFile.getContentType())
        || !FILE_TYPE.equals(csvFile.getContentType())) {
      log.info("File is not in csv format!!");
      return;
    }
    httpResponse.setContentType(FILE_TYPE);
    httpResponse.setHeader(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
    List<Animal> animals = openCSVService.readFromCSVFile(csvFile);
   // openCSVService.exportToCSVFile(animals, httpResponse);
    openCSVService.exportToCSVFile(animals);
    log.info("Processing finished in {}", System.currentTimeMillis() - startTime);
  }
}
