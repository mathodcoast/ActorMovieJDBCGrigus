package com.grigus.model;

import lombok.*;

import java.time.Duration;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode //(exclude = "id")
@ToString
@Builder
public class Movie {
    Long id;
    String name;
    Duration duration;
    LocalDate releaseDate;
}
