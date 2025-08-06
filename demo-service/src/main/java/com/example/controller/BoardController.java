package com.example.controller;

import com.example.entity.Board;
import com.example.spec.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public Board createBoard(@RequestBody Board board) {
        return boardService.createBoard(board);
    }

    @GetMapping
    public List<Board> getAllBoards() {
        return boardService.getAllBoards();
    }

    @GetMapping("/{id}")
    public Board getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

    @GetMapping("/search")
    public List<Board> searchBoards(@RequestParam String title) {
        return boardService.searchBoardsByTitle(title);
    }

    @GetMapping("/users/{userId}/boards")
    public List<Board> getBoardsByUser(@PathVariable Long userId) {
        return boardService.getBoardsByUser(userId);
    }

    @PutMapping("/{id}")
    public Board updateBoard(@PathVariable Long id, @RequestBody Board boardDetails, @RequestParam Long currentUserId) {
        return boardService.updateBoard(id, boardDetails, currentUserId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id, @RequestParam Long currentUserId) {
        boardService.deleteBoard(id, currentUserId);
        return ResponseEntity.ok().build();
    }
}
