package de.knowwe.caseTrain;

public class Einleitung extends BlockMarkupType {

	public Einleitung() {
		super("Einleitung");
		this.addContentType(new Bild());

	}

	@Override
	public String getCSSClass() {
		return "Ie";
	}

}

