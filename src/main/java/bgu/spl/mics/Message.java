package bgu.spl.mics;

/**
 * A message is a data-object which is passed between micro-services as a means
 * of communication. The Message interface is a "Marker" interface which means
 * that it is used only to mark other types of objects as messages. It does not
 * contain any methods but every class that you want to send as a message (using
 * the {@link MessageBus}) must implement it.
 */
public interface Message {

}
/*       {
            "name": "Nala",
            "department": "Computer Science",
            "status": "PhD",
            "models":
            [
                {
                    "name": "YOLO9000",
                    "type": "Images",
                    "size": 100000
                },
                {
                    "name": "VIT2",
                    "type": "Images",
                    "size": 1000000
                },
                {
                    "name": "MuchMoreEfficientNet",
                    "type": "Images",
                    "size": 20000
                },
                {
                    "name": "DenserNet",
                    "type": "Images",
                    "size": 100000
                }
            ]

        }

 */