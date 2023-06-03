package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.sql.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Genres {
    private Integer id;
/*    public static void main(String[] args) {
        Film film = new Film();
        film.getGenres().genres.add(new HashMap<>(){{
            put("id", 1L);
        }});
        film.getGenres().genres.add(new HashMap<>(){{
            put("id", 2L);
        }});
        film.getGenres().genres.add((new HashMap<>(){{
            put("id", 3L);
        }}));
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(film));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }*/
}
