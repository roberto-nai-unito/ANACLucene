
/**
 * 
 * Class for the type of search (CIG, Public entity CF, Public entity DENOMINATION, Private company CF, Private company DENOMINATION)
 *
 */
public class Research 
{
	
	ResearchType researchType;
	
	// Constructor
	public Research(ResearchType researchType) 
	{
        this.researchType = researchType;
    }
	
	// Getter e Setter
	public ResearchType getResearchType() {
		return researchType;
	}


	public void setResearchType(ResearchType researchType) {
		this.researchType = researchType;
	}


	@Override
	public String toString()
	{
		switch (researchType) 
		{
			case CIG:
				return "CIG";
			case APP_CF:
				return "APP_CF";
			case APP_DEN:
				return "APP_DEN";
			case AGG_CF:
				return "AGG_CF";
			case AGG_DEN:
				return "AGG_DEN";
			default:
				return null;
		}
		
	}
	
}
