package com.example.controller;

import com.example.dto.response.ApiResponse;
import com.example.entity.Board;
import com.example.spec.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ApiResponse<Board> createBoard(@RequestBody Board board) {
        return ApiResponse.success(boardService.createBoard(board));
    }

    @GetMapping
    public ApiResponse<List<Board>> getAllBoards() {
        return ApiResponse.success(boardService.getAllBoards());
    }

    @GetMapping("/{id}")
    public ApiResponse<Board> getBoard(@PathVariable Long id) {
        return ApiResponse.success(boardService.getBoard(id));
    }

    @GetMapping("/search")
    public ApiResponse<List<Board>> searchBoards(@RequestParam String title) {
        return ApiResponse.success(boardService.searchBoardsByTitle(title));
    }

    @GetMapping("/users/{userId}/boards")
    public ApiResponse<List<Board>> getBoardsByUser(@PathVariable Long userId) {
        return ApiResponse.success(boardService.getBoardsByEmployee(userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<Board> updateBoard(@PathVariable Long id, @RequestBody Board boardDetails, @RequestParam Long currentUserId) {
        return ApiResponse.success(boardService.updateBoard(id, boardDetails, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBoard(@PathVariable Long id, @RequestParam Long currentUserId) {
        boardService.deleteBoard(id, currentUserId);
        return ApiResponse.success(null);
    }
}
