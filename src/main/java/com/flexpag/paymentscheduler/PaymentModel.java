package com.flexpag.paymentscheduler;

import com.flexpag.paymentscheduler.PaymentStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.Instant;

@Entity                     //Determina que essa classe modela um objeto
@Data                       //Junta @Getter, @Setter e @RequiredArgsConstructor para poder manipular os elementos de classe
@AllArgsConstructor         //Gera construtor que aceita um argumento para cada campo
@NoArgsConstructor          //Gera construtor sem necessidade de argumentos
public class PaymentModel implements Serializable {
  private static final long serialVersionUID = 1L; //versão 1 da serialização da classe

  @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //gera automaticamente valor para identificação
	private Long id;
	private String name;
	private Double payment;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone="GMT-3") //formato de data e hora de brasília
	private Instant date;
	@NonNull //torna parâmetro status obrigatório
	private PaymentStatus status;

  public PaymentModel(Long id, String name, Double payment, Instant date) { //construtor da classe
		this.id = id;
		this.name = name;
		this.payment = payment;
		this.date = date;
		this.status = PaymentStatus.PENDING;     //inicializa status como pending por padrão
	}
}