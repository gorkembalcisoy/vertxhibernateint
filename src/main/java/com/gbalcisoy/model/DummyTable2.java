package com.gbalcisoy.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "dummy_table_2")
@Data
public class DummyTable2 extends PersistableEntity {

    @Column
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dummy_table_id", foreignKey = @ForeignKey(name = "dummy_table_2_dummy_table_id_fkey"))
    private DummyTable dummyTable;
}
