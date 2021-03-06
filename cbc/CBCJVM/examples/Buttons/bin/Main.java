import cbccore.Device;
import cbccore.events.Event;
import cbccore.events.EventManager;
import cbccore.events.EventListenerAdapter;
import cbccore.sensors.buttons.ButtonEmitter;

public class Main {
	
	public void run() {
		EventManager manager = EventManager.get();
		ButtonEmitter.get().start();
		
		
		
		
		EventListenerAdapter AButtonPressedHandler = new EventListenerAdapter() {
			@Override
			public void event(Event type) {
				System.out.println("The A Button Was Pressed!");
			}
		};
		
		EventListenerAdapter AButtonReleasedHandler = new EventListenerAdapter() {
			@Override
			public void event(Event type) {
				System.out.println("The A Button Was Released!");
			}
		};
		
		manager.connect(ButtonEmitter.AButtonPressed, AButtonPressedHandler);
		manager.connect(ButtonEmitter.AButtonReleased, AButtonReleasedHandler);
	}
	
	
	
	
	public static void main(String[] args) {
		cbccore.Device.init();
		System.out.println("Press the A button.");
		new Main().run();
	}
}
