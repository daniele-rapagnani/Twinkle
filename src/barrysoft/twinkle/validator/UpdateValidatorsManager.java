package barrysoft.twinkle.validator;

public class UpdateValidatorsManager 
{
	private static final UpdateValidator[] validators = new UpdateValidator[] {
		MD5UpdateValidator.getInstance(),
		DSAUpdateValidator.getInstance()
	};
	
	public static UpdateValidator[] getDefaultValidators()
	{
		return validators;
	}
}
