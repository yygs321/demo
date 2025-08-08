package com.example.service;

import com.example.entity.Board;
import com.example.exception.BusinessException;
import com.example.exception.BoardNotFoundException;
import com.example.exception.BusinessException;
import com.example.exception.UnauthorizedException;
import com.example.mapper.BoardMapper;
import com.example.spec.BoardService;
import com.example.spec.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;
    private final EmployeeService employeeService;

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
    public List<Board> getBoardsByEmployee(Long employeeId) {
        log.info("Finding boards by employeeId: {}", employeeId);
        return boardMapper.findByEmployeeId(employeeId);
    }

    @Transactional(noRollbackFor = BusinessException.class)
    @Override
    public Board createBoard(Board board) {
        employeeService.checkEmployeeExists(board.getEmployee().getId());
        boardMapper.save(board);
        log.info("New board created with id: {}", board.getId());
        return board;
    }

    @Transactional
    @Override
    public Board updateBoard(Long boardId, Board boardDetails, Long currentEmployeeId) {
        Board existingBoard = getOwnedBoard(boardId, currentEmployeeId);

        Board updatedBoard = Board.builder()
                .id(boardId)
                .title(boardDetails.getTitle())
                .content(boardDetails.getContent())
                .employee(existingBoard.getEmployee())
                .build();
        boardMapper.update(updatedBoard);
        log.info("Board with id: {} updated successfully", boardId);
        return updatedBoard;
    }

    @Transactional
    @Override
    public void deleteBoard(Long boardId, Long currentEmployeeId) {
        getOwnedBoard(boardId, currentEmployeeId);
        boardMapper.deleteById(boardId);
        log.info("Board with id: {} deleted successfully", boardId);
    }

    private Board getOwnedBoard(Long boardId, Long currentEmployeeId) {
        return boardMapper.findOwnBoard(boardId, currentEmployeeId)
                .orElseThrow(() -> new UnauthorizedException("Board not found or you are not the owner."));
    }
}