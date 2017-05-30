public class Activity {
	
	String type;
	int delay;
	int resource;
	int value;
	
	//Empty constructor
	public Activity() {
		type = "";
		delay = 0;
		resource = 0;
		value = 0;
	}
	
	// Constructor to set activity variables to provided information
	public Activity(String activity, int delay, int resource, int value) {
		this.type = activity;
		this.delay = delay;
		this.resource = resource;
		this.value = value;
	}
	
	
	

}
