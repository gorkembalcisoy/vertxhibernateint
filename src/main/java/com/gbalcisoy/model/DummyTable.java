package com.gbalcisoy.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dummy_table")
@Data
public class DummyTable extends PersistableEntity {

    @Column
    private String name;
}
