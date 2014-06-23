package im.tox.antox.callbacks;

import android.content.Context;
import android.util.Log;

import im.tox.antox.data.AntoxDB;
import im.tox.antox.tox.ToxSingleton;
import im.tox.antox.utils.AntoxFriend;
import im.tox.jtoxcore.callbacks.OnFileControlCallback;
import im.tox.jtoxcore.ToxFileControl;
import im.tox.jtoxcore.callbacks.OnFileSendRequestCallback;

public class AntoxOnFileControlCallback implements OnFileControlCallback<AntoxFriend> {

    private static final String TAG = "OnFileControlCallback";
    private Context ctx;
    ToxSingleton toxSingleton = ToxSingleton.getInstance();

    public AntoxOnFileControlCallback(Context ctx) { this.ctx = ctx; }

    public void execute(AntoxFriend friend, boolean sending, int fileNumber, ToxFileControl control_type, byte[] data) {
        Log.d(TAG, "execute, control type: " + control_type.name() + " sending: " + sending);
        if (control_type.equals(ToxFileControl.TOX_FILECONTROL_FINISHED) && !sending) {
            Log.d(TAG, "TOX_FILECONTROL_FINISHED");
            toxSingleton.fileFinished(friend.getId(), fileNumber, ctx);
        }
        if (control_type.equals(ToxFileControl.TOX_FILECONTROL_ACCEPT) && sending) {
            AntoxDB antoxDB = new AntoxDB(ctx);
            antoxDB.fileTransferStarted(friend.getId(), fileNumber);
            antoxDB.close();
            toxSingleton.updatedMessagesSubject.onNext(true);
            toxSingleton.sendFileData(friend.getId(), fileNumber, 0, ctx);
        }
    }
}
