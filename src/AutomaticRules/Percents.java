package AutomaticRules;

import java.util.List;

public class Percents implements Comparable<Percents>{
	
		private String atributo;
		private List<Integer> registros;
		private int tamanho;
		private float percentIndividual;
		private List<Percents> percentPares;

		public Percents(String atributo, List<Integer> registros) {			
			this.atributo = atributo;
			this.registros = registros;
		}
		
		public Percents(String atributo, float percent) {
			this.atributo = atributo;
			this.percentIndividual = percent;
		}
			
		public String getAtributo() {
			return atributo;
		}

		public void setAtributo(String atributo) {
			this.atributo = atributo;
		}

		public List<Integer> getRegistros() {
			return registros;
		}

		public void setRegistros(List<Integer> registros) {
			this.registros = registros;
		}

		public int getTamanho() {
			return tamanho;
		}

		public void setTamanho(int tamanho) {
			this.tamanho = tamanho;
		}

		public float getPercentIndividual() {
			return percentIndividual;
		}

		public void setPercentIndividual(float percentIndividual) {
			this.percentIndividual = percentIndividual;
		}

		public List<Percents> getPercentPares() {
			return percentPares;
		}

		public void setPercentPares(List<Percents> percentPares) {
			this.percentPares = percentPares;
		}

		@Override
		public int compareTo(Percents outro) {
			if (this.percentIndividual < outro.percentIndividual) {
	            return 1;
	        }
	        if (this.percentIndividual > outro.percentIndividual) {
	            return -1;
	        }
	        return 0;
		}
		
		@Override
		public boolean equals(Object obj) {						
			if (((Percents)obj).getAtributo().equals(this.atributo)) {
				return true;
			} else {
				return false;
			}
		}
}
