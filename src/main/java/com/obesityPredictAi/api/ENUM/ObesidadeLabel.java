package com.obesityPredictAi.api.ENUM;

    public enum ObesidadeLabel {
        UNDERWEIGHT(1, "Abaixo do peso"),
        NORMAL_WEIGHT(2, "Peso normal"),
        OVERWEIGHTI(3, "Sobrepeso Grau I"),
        OVERWEIGHTII(4, "Sobrepeso Grau II"),
        OBESITYI(5, "Obesidade Grau I"),
        OBESITYII(6, "Obesidade Grau II"),
        OBESITYIII(7, "Obesidade Grau III");

        private final int code;
        private final String descricao;

        ObesidadeLabel(int code, String descricao) {
            this.code = code;
            this.descricao = descricao;
        }

        public int getCode() {
            return code;
        }

        public String getDescricao() {
            return descricao;
        }

        public static ObesidadeLabel fromCode(int code) {
            for (ObesidadeLabel label : values()) {
                if (label.getCode() == code) {
                    return label;
                }
            }
            throw new IllegalArgumentException("Código inválido para ObesidadeLabel: " + code);
        }
    }

