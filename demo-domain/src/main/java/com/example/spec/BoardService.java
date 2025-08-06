package com.example.spec;

import com.example.entity.Board;
import java.util.List;

public interface BoardService {
    Board getBoard(Long boardId);
    List<Board> getAllBoards();
    List<Board> searchBoardsByTitle(String title);
    List<Board> getBoardsByUser(Long userId);
    Board createBoard(Board board);
    Board updateBoard(Long boardId, Board boardDetails, Long currentUserId);
    void deleteBoard(Long boardId, Long currentUserId);
}