package com.gotravel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ResenaId implements java.io.Serializable {

    private static final long serialVersionUID = -3017767775810651976L;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "id_contratacion", nullable = false)
    private Integer idContratacion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ResenaId entity = (ResenaId) o;
        return Objects.equals(this.idUsuario, entity.idUsuario) &&
                Objects.equals(this.idContratacion, entity.idContratacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idContratacion);
    }

}