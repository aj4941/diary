package com.squarecross.photoalbum.service;

import com.squarecross.photoalbum.domain.Album;
import com.squarecross.photoalbum.dto.AlbumDto;
import com.squarecross.photoalbum.mapper.AlbumMapper;
import com.squarecross.photoalbum.repository.AlbumRepository;
import com.squarecross.photoalbum.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static com.squarecross.photoalbum.service.Constants.*;

@Service
public class AlbumService {
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private PhotoRepository photoRepository;
    public AlbumDto getAlbum(Long albumId) {
        Optional<Album> res = albumRepository.findById(albumId);
        if (res.isPresent()) {
            AlbumDto albumDto = AlbumMapper.convertToDto(res.get());
            albumDto.setCount(photoRepository.countByAlbum_AlbumId(albumId));
            return albumDto;
        }
        else
            throw new EntityNotFoundException
                    (String.format("앨범 아이디 %d로 조회되지 않았습니다.", albumId));
    }
    public Album getAlbumByName(String albumName) {
        Optional<Album> res = Optional.ofNullable(albumRepository.findByAlbumName(albumName));
        if (res.isPresent())
            return res.get();
        else
            throw new EntityNotFoundException(
                    String.format("앨범 이름 %s로 조회되지 않았습니다.", albumName));
    }

    public AlbumDto createAlbum(AlbumDto albumDto) throws IOException {
        Album album = AlbumMapper.convertToModel(albumDto);
        albumRepository.save(album);
        createAlbumDirectories(album);
        return AlbumMapper.convertToDto(album);
    }

    private void createAlbumDirectories(Album album) throws IOException {
        Files.createDirectories(Paths.get(PATH_PREFIX + "/photos/original" + album.getAlbumId()));
        Files.createDirectories(Paths.get(PATH_PREFIX + "photos/thumb" + album.getAlbumId()));
    }
}
