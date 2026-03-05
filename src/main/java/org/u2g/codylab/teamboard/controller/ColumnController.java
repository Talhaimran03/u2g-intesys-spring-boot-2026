package org.u2g.codylab.teamboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.ColumnApi;
import org.u2g.codylab.teamboard.dto.ColumnResponseApiDTO;
import org.u2g.codylab.teamboard.dto.CreateColumnRequestApiDTO;
import org.u2g.codylab.teamboard.dto.UpdateColumnRequestApiDTO;
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
        List<ColumnResponseApiDTO> columns = columnService.getAll();
        return ResponseEntity.ok(columns);
    }

    @Override
    public ResponseEntity<ColumnResponseApiDTO> getColumnById(Long id) {
        ColumnResponseApiDTO response = columnService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ColumnResponseApiDTO> createColumn(CreateColumnRequestApiDTO request) {
        ColumnResponseApiDTO response = columnService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ColumnResponseApiDTO> updateColumn(Long id, UpdateColumnRequestApiDTO updateColumnRequestApiDTO) {
        ColumnResponseApiDTO response = columnService.update(id, updateColumnRequestApiDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteColumnById(Long id) {
        columnService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
