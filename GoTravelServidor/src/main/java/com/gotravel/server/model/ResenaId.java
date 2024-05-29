package com.gotravel.server.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ResenaId implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 5465454492221406391L;

    @Column(name = "id_usuario", nullable = false)
    @Expose
    private Integer idUsuario;

    @Column(name = "id_servicio", nullable = false)
    @Expose
    private Integer idServicio;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ResenaId entity = (ResenaId) o;
        return Objects.equals(this.idUsuario, entity.idUsuario) &&
                Objects.equals(this.idServicio, entity.idServicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idServicio);
    }

}