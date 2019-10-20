package com.gbalcisoy.model;

import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@MappedSuperclass
public class PersistableEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    @Setter
    private UUID id;
}
