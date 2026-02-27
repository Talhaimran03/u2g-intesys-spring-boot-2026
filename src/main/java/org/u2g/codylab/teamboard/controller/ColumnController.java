package org.u2g.codylab.teamboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.ColumnApi;
import org.u2g.codylab.teamboard.dto.ColumnRequestApiDTO;
import org.u2g.codylab.teamboard.dto.ColumnResponseApiDTO;
import org.u2g.codylab.teamboard.service.ColumnService;

import java.util.List;

@RestController
public class ColumnController implements ColumnApi {
    private final ColumnService columnService;

    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }

    @Override
    public ResponseEntity<List<ColumnResponseApiDTO>> getAllColumns() {
        return ResponseEntity.ok(columnService.getAll());
    }

    @Override
    public ResponseEntity<ColumnResponseApiDTO> getColumnById(Long id) {
        return ResponseEntity.ok(columnService.getById(id));
    }

    @Override
    public ResponseEntity<ColumnResponseApiDTO> createColumn(ColumnRequestApiDTO request) {
        return new ResponseEntity<>(columnService.create(request), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ColumnResponseApiDTO> updateColumnById(Long id, ColumnRequestApiDTO request) {
        return ResponseEntity.ok(columnService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> deleteColumnById(Long id) {
        columnService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

