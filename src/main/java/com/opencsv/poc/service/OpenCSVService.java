package com.opencsv.poc.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.opencsv.poc.model.Animal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class OpenCSVService {

  public List<Animal> readFromCSVFile(MultipartFile csvFile) throws IOException {

    List<Animal> animals;
    try (Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
      CsvToBean<Animal> csvToBean =
          new CsvToBeanBuilder(reader)
              .withType(Animal.class)
              .withIgnoreLeadingWhiteSpace(true)
              .withSkipLines(1)
              .build();
      animals = csvToBean.parse();
    }
    if (!CollectionUtils.isEmpty(animals)) {
      log.debug("Number of records fetched from csv file {}", animals.size());
      return animals;
    } else {
      return null;
    }
  }

  //export to CSV with httpResponse
  public void exportToCSVFile(List<Animal> animals, HttpServletResponse httpResponse)
      throws IOException {

    try {
      ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy();
      mappingStrategy.setType(Animal.class);
      StatefulBeanToCsvBuilder<Animal> builder =
          new StatefulBeanToCsvBuilder(httpResponse.getWriter());
      StatefulBeanToCsv beanWriter = builder.withMappingStrategy(mappingStrategy).build();
      beanWriter.write(animals);
      httpResponse.getWriter().flush();
      httpResponse.getWriter().close();
    } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      log.error("error writing to csv file", e);
    }
  }

  //export to CSV in local file
  public void exportToCSVFile(List<Animal> animals) throws IOException {

    try {

      Writer writer = Files.newBufferedWriter(Paths.get("final.csv"));

      StatefulBeanToCsv<Animal> csvWriter =
          new StatefulBeanToCsvBuilder<Animal>(writer)
              .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
              .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
              .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
              .withLineEnd(CSVWriter.DEFAULT_LINE_END)
              .withOrderedResults(true)
              .build();

      csvWriter.write(animals);
      // writer.close();

    } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      log.error("error writing to csv file", e);
    }
  }
}
