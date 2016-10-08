package parser;

class Symbol
	{
	private int symbolType; // 0 stands for NULL, 1 stands for T, 2 stands for NT
	private String text;

	public Symbol(int symbolType, String text)
		{
		this.symbolType = symbolType;
		this.text = text;
		}

	}