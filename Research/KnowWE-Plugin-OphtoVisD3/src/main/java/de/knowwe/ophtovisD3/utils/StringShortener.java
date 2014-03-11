package de.knowwe.ophtovisD3.utils;

public class StringShortener {

	private final ElliminationType type;
	private final int targetSize;

	public StringShortener(ElliminationType type, int targetsize) {
		this.type = type;
		this.targetSize = targetsize;
	}

	public String shorten(String startword) {
		String result = startword;
		if (startword.length() > targetSize) {
			switch (type) {
				case NORMAL:
					result = startword.substring(0, targetSize);
					result += "..";
					break;
				case VOCALELIMIATION:
					//System.out.println("Vokalelimination " + targetSize );
					int currentPosition = result.length();
					while (result.length() > targetSize) {
						currentPosition--;
						//	System.out.println(result + " " + currentPosition);
						char current = result.charAt(currentPosition);
						if (current == 'a' || current == 'A' || current == 'e' || current == 'E' || current == 'i' || current == 'I' || current == 'o' || current == 'O' || current == 'u' || current == 'U') {
							//System.out.println("respos"+result + " " + currentPosition);
							result = result.substring(0, currentPosition) + result.substring(currentPosition + 1, result
									.length());
						}
						if (currentPosition == 0) {
							result = result.substring(0, targetSize);
							result += "..";
							break;
						}
					}
					result += "..";
					break;
				case MIDDLE:
					int dif = startword.length() - targetSize;
					if (dif % 2 == startword.length() % 2) {
						result = startword.substring(0, targetSize / 2) + "..." + startword.substring(targetSize / 2 + dif, startword
								.length());
					}
					else {
						//	System.out.println("start = " + startword);
						result = startword.substring(0, targetSize / 2 - 1) + "..." + startword.substring(targetSize / 2 + dif, startword
								.length());
					}
					break;
				case MOSTUSEDLETTERS:
					break;

			}
			//System.out.println("Länge von " + result + " ist " +result.length());
		}
		return result;

	}

	public enum ElliminationType {
		NORMAL, VOCALELIMIATION, MIDDLE, MOSTUSEDLETTERS
	}

}
