package com.gotravel.cliente.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ResenaId implements Serializable {

    // El serialVersionUID es un valor que se asocia con cada clase serializable en tiempo de ejecución. Es un número único que representa la versión de la clase.
    // Durante la deserialización, Java verifica si el serialVersionUID del objeto serializado coincide con el serialVersionUID de la clase local.
    // Si no coinciden, se lanza una excepción InvalidClassException.
    private static final long serialVersionUID = -3017767775810651976L;

    @Column(name = "idUsuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "idContratacion", nullable = false)
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