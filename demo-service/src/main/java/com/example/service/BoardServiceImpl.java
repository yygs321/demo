package com.example.service;

import com.example.entity.Board;
import com.example.exception.BoardNotFoundException; // 추가
import com.example.exception.UserNotFoundException; // 유지
import com.example.mapper.BoardMapper;
import com.example.spec.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    @Override
    public Board getBoard(Long boardId) {
        log.info("Finding board by id: {}", boardId);
        return boardMapper.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found with id: " + boardId));
    }

    @Override
    public List<Board> getAllBoards() {
        log.info("Finding all boards");
        return boardMapper.findAll();
    }

    @Override
    public List<Board> searchBoardsByTitle(String title) {
        log.info("Finding boards by title containing: {}", title);
        return boardMapper.findByTitleContaining(title);
    }

    @Override
    public List<Board> getBoardsByUser(Long userId) {
        log.info("Finding boards by userId: {}", userId);
        return boardMapper.findByUserId(userId);
    }

    @Override
    public Board createBoard(Board board) {
        boardMapper.save(board);
        log.info("New board created with id: {}", board.getId());
        return board;
    }

    @Override
    public Board updateBoard(Long boardId, Board boardDetails, Long currentUserId) {
        Board existingBoard = getOwnedBoard(boardId, currentUserId);

        Board updatedBoard = Board.builder()
                .id(boardId)
                .title(boardDetails.getTitle())
                .content(boardDetails.getContent())
                .user(existingBoard.getUser())
                .build();
        boardMapper.update(updatedBoard);
        log.info("Board with id: {} updated successfully", boardId);
        return updatedBoard;
    }

    @Override
    public void deleteBoard(Long boardId, Long currentUserId) {
        getOwnedBoard(boardId, currentUserId);
        boardMapper.deleteById(boardId);
        log.info("Board with id: {} deleted successfully", boardId);
    }

    private Board getOwnedBoard(Long boardId, Long currentUserId) {
        return boardMapper.findOwnBoard(boardId, currentUserId)
                .orElseThrow(() -> new SecurityException("Board not found or you are not the owner."));
    }
}