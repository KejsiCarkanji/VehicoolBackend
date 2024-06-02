package com.vehicool.vehicool.api.controller;

import com.vehicool.vehicool.business.service.DataPoolService;
import com.vehicool.vehicool.business.service.StorageService;
import com.vehicool.vehicool.persistence.entity.DataPool;
import com.vehicool.vehicool.util.mappers.ResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.vehicool.vehicool.util.constants.Messages.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/dataPool")
public class DataPoolController {
    private final DataPoolService dataPoolService;

    @GetMapping("/get-enum-labels/{enumName}")
    public ResponseEntity<Object> get(@PathVariable String enumName)  {
        try {
            List<DataPool> labels = dataPoolService.findAllByEnumName(enumName);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, labels, RECORDS_RECEIVED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);

        }
    }
}
