package com.example.controller;

import com.example.response.ApiResponse;
import com.example.entity.Board;
import com.example.spec.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ApiResponse<Board> createBoard(@Valid @RequestBody Board board) {
        return ApiResponse.success("Board created successfully", boardService.createBoard(board));
    }

    @GetMapping
    public ApiResponse<List<Board>> getAllBoards() {
        return ApiResponse.success("All boards retrieved successfully", boardService.getAllBoards());
    }

    @GetMapping("/{id}")
    public ApiResponse<Board> getBoard(@PathVariable Long id) {
        return ApiResponse.success("Board retrieved successfully", boardService.getBoard(id));
    }

    @GetMapping("/search")
    public ApiResponse<List<Board>> searchBoards(@RequestParam String title) {
        return ApiResponse.success("Boards searched successfully", boardService.searchBoardsByTitle(title));
    }

    @GetMapping("/users/{userId}/boards")
    public ApiResponse<List<Board>> getBoardsByUser(@PathVariable Long userId) {
        return ApiResponse.success("Boards by user retrieved successfully", boardService.getBoardsByEmployee(userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<Board> updateBoard(@PathVariable Long id, @Valid @RequestBody Board boardDetails, @RequestParam Long currentUserId) {
        return ApiResponse.success("Board updated successfully", boardService.updateBoard(id, boardDetails, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBoard(@PathVariable Long id, @RequestParam Long currentUserId) {
        boardService.deleteBoard(id, currentUserId);
        return ApiResponse.success("Board deleted successfully");
    }
}
