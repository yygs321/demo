package com.example.mapper;

import com.example.entity.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {
    Optional<Board> findById(Long id);
    List<Board> findAll();
    List<Board> findByUserId(Long userId);
    List<Board> findByTitleContaining(String title);
    Optional<Board> findOwnBoard(@Param("boardId") Long boardId, @Param("userId") Long userId);
    void save(Board board);
    void update(Board board);
    void deleteById(Long id);
}
