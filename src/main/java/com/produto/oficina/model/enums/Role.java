package com.produto.oficina.model.enums;

public enum Role {
    ADMIN("Administrador"),
    DEV("Desenvolvedor"),
    USER("Usuário");

    public final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }

    public static Role[] onlyNormalRoles() {
        return new Role[]{USER, ADMIN};
    }

}
