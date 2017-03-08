package omrecorder;

import android.media.AudioRecord;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by singun on 2017/3/8 0008.
 */

public class ExtraPullTransport extends PullTransport.AbstractPullTransport {

    private final WriteAction writeAction;
    private final ProcessAction processAction;

    public ExtraPullTransport(AudioSource audioRecordSource,
                   OnAudioChunkPulledListener onAudioChunkPulledListener,
                              WriteAction writeAction, ProcessAction processAction) {
        super(audioRecordSource, onAudioChunkPulledListener);
        this.writeAction = writeAction;
        this.processAction = processAction;
    }

    public ExtraPullTransport(AudioSource audioRecordSource, ProcessAction processAction) {
        this(audioRecordSource, null, new WriteAction.Default(), processAction);
    }

    @Override void startPoolingAndWriting(AudioRecord audioRecord, int minimumBufferSize,
                                          OutputStream outputStream) throws IOException {
        while (audioRecordSource.isEnableToBePulled()) {
            int size = minimumBufferSize / 2;
            AudioChunk audioChunk = new AudioChunk.Shorts(new short[size]);
            int length = audioRecord.read(audioChunk.toShorts(), 0, size);
            if (length > 0) {
                if (onAudioChunkPulledListener != null) {
                    postPullEvent(audioChunk);
                }
                processAction.processData(audioChunk, length);
                writeAction.execute(audioChunk.toBytes(), outputStream);
            }
        }
    }

    public interface ProcessAction {
        void processData(AudioChunk audioChunk, int length);
    }
}
