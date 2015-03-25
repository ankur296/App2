package corp.seedling.movie.guess.utils;

import java.util.Vector;

import corp.seedling.movie.guess.ui.GameScreen;

import android.os.Handler;
import android.os.Message;

public abstract class PauseHandler extends Handler {
	final Vector<Message> messageQueueBuffer = new Vector<Message>();
	private boolean paused;

	final public void resume() {
		paused = false;
		int i = 1;

		while (messageQueueBuffer.size() > 0) {
			final Message msg = messageQueueBuffer.elementAt(0);
			messageQueueBuffer.removeElementAt(0);
			System.out.println("ankur send delayed msg # "+i);
			if (i == 1)
				sendMessage(msg );
			else
				sendMessageDelayed(msg , GameScreen.ANIM_DUR * i++);
		}
	}

	final public void pause() {
		paused = true;
	}

	protected abstract boolean storeMessage(Message message);
	protected abstract void processMessage(Message message);

	@Override
	final public void handleMessage(Message msg) {
		if (paused) {
			if (storeMessage(msg)) {
				Message msgCopy = new Message();
				msgCopy.copyFrom(msg);
				messageQueueBuffer.add(msgCopy);
			}
		} else {
			processMessage(msg);
		}
	}
}