package com.trainingcenter.controller;

import com.trainingcenter.dto.SearchResultDTO;
import com.trainingcenter.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * Public search endpoint for guests
     * GET /api/search?q=keyword&type=all
     */
    @GetMapping
    public ResponseEntity<List<SearchResultDTO>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "all") String type) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<SearchResultDTO> results;

        switch (type.toLowerCase()) {
            case "courses":
                results = searchService.searchCourses(q);
                break;
            case "exams":
                results = searchService.searchExams(q);
                break;
            case "all":
            default:
                results = searchService.searchAll(q);
                break;
        }

        return ResponseEntity.ok(results);
    }
}
