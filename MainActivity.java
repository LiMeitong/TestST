package com.course.testst;

import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.smp.soundtouchandroid.SoundTouch;

import java.io.IOException;
import java.io.InputStream;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {


    private Button oriBtn,simpleBtn, pitchBtn,temBtn,mixBtn ;
    private SeekBar resampleSeek, pitchSeek, timeSeek;
    private byte[] originaldata;
    private int resampleRate = 8000;
    private float pitchRate = 1.0f;
    private float scaleRate = 1.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oriBtn = (Button) findViewById(R.id.OriButton);

        simpleBtn = (Button) findViewById(R.id.SimpleBtn);
        pitchBtn = (Button) findViewById(R.id.pitchShiftBtn);
        temBtn = (Button) findViewById(R.id.TemBtn);

        resampleSeek = (SeekBar) findViewById(R.id.seekBarResample);
        pitchSeek = (SeekBar) findViewById(R.id.seekBarPitchShift);
        timeSeek = (SeekBar) findViewById(R.id.seekBarTimeScale);


        timeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                scaleRate = (float)timeSeek.getProgress()/100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pitchSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pitchRate = (float)pitchSeek.getProgress()/100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        resampleSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * change resample
             * @param seekBar
             * @param i
             * @param b
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int resampleInput = resampleSeek.getProgress();
                    resampleRate = (int) (resampleRate* ((double)(resampleInput)/300));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        mixBtn = (Button) findViewById(R.id.MixBtn);


        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        oriBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playOri("OriAudio.wav");
            }
        });

        /**
         * re-sample  change  by resample
         */
        simpleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int len = originaldata.length;
                assert (len>0);

                int lenAfter = len;
                byte[] simpleData = new byte[lenAfter];

                for( int i = 0 ; i < len;i ++  ){
                    simpleData[i] =originaldata[i];
                }


                /**
                 * default resample rate
                 */
                resampleRate = 8000;

                /**
                 * update resample rate
                 */
                int resampleInput = resampleSeek.getProgress();
                if( resampleInput != 300 ){
                    resampleRate = (int) (resampleRate* ((double)(resampleInput+300)/300));
                }

                AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        resampleRate, AudioFormat.CHANNEL_OUT_DEFAULT,
                        AudioFormat.ENCODING_PCM_16BIT, lenAfter, AudioTrack.MODE_STATIC);
                mAudioTrack.write(simpleData,0,simpleData.length);
                mAudioTrack.play();
            }
        });

        /**
         *  pitch shifting
         */


        pitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                pitchRate = (float)pitchSeek.getProgress()/100;
                SoundTouch soundTouch = new SoundTouch(0, AudioFormat.CHANNEL_OUT_DEFAULT, 8000, 2, 1.0f, pitchRate);


                byte[] input = originaldata;
                soundTouch.putBytes(input);

                //write output to a byte[]:
                byte[] output = new byte[input.length] ;
                int bytesReceived = soundTouch.getBytes(output);

                //after you write the last byte[], call finish().
                soundTouch.finish();

                //now get the remaining bytes from the sound processor.
                bytesReceived = 0;

                AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        8000, AudioFormat.CHANNEL_OUT_DEFAULT,
                        AudioFormat.ENCODING_PCM_16BIT, output.length, AudioTrack.MODE_STATIC);
                mAudioTrack.write(output,0,output.length);
                mAudioTrack.play();


                do
                {
                    bytesReceived = soundTouch.getBytes(output);
                    //do stuff with output.
                } while (bytesReceived != 0);

                //if you stop playing, call clear on the track to clear the pipeline for later use.
                soundTouch.clearBuffer();




            }
        });


        /**
         * time scaling
         */
        temBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scaleRate = (float)timeSeek.getProgress()/100;
                SoundTouch soundTouch = new SoundTouch(0, AudioFormat.CHANNEL_OUT_DEFAULT, 8000, 2, scaleRate, 1.0f);


                byte[] input = originaldata;
                soundTouch.putBytes(input);

                //write output to a byte[]:
                byte[] output = new byte[input.length] ;
                int bytesReceived = soundTouch.getBytes(output);

                //after you write the last byte[], call finish().
                soundTouch.finish();

                //now get the remaining bytes from the sound processor.
                bytesReceived = 0;

                AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        8000, AudioFormat.CHANNEL_OUT_DEFAULT,
                        AudioFormat.ENCODING_PCM_16BIT, output.length, AudioTrack.MODE_STATIC);
                mAudioTrack.write(output,0,output.length);
                mAudioTrack.play();


                do
                {
                    bytesReceived = soundTouch.getBytes(output);
                    //do stuff with output.
                } while (bytesReceived != 0);

                //if you stop playing, call clear on the track to clear the pipeline for later use.
                soundTouch.clearBuffer();


            }
        });

        mixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SoundTouch soundTouch = new SoundTouch(0, AudioFormat.CHANNEL_OUT_DEFAULT, 8000, 2, scaleRate, pitchRate);


                byte[] input = originaldata;
                soundTouch.putBytes(input);

                //write output to a byte[]:
                byte[] output = new byte[input.length] ;
                int bytesReceived = soundTouch.getBytes(output);

                //after you write the last byte[], call finish().
                soundTouch.finish();

                //now get the remaining bytes from the sound processor.
                bytesReceived = 0;

                AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        8000, AudioFormat.CHANNEL_OUT_DEFAULT,
                        AudioFormat.ENCODING_PCM_16BIT, output.length, AudioTrack.MODE_STATIC);
                mAudioTrack.write(output,0,output.length);
                mAudioTrack.play();


                do
                {
                    bytesReceived = soundTouch.getBytes(output);
                    //do stuff with output.
                } while (bytesReceived != 0);

                //if you stop playing, call clear on the track to clear the pipeline for later use.
                soundTouch.clearBuffer();

            }
        });


    }



    public void playOri( String filename ) {
        AssetFileDescriptor afd ;
        try {
            MediaPlayer player;
            afd = getAssets().openFd(filename);
            player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 读入原始音频数据
     * @throws java.io.IOException
     */
    public void loadData() throws IOException {

        int length;
        InputStream fin = getAssets().open("OriAudio.wav");
        /**
         * 去掉文件头
         */
        length = fin.available() - 44;
        originaldata = null;
        byte [] buffer = new byte[length + 44];
        originaldata = new byte[length];
        fin.read(buffer);
        fin.close();
        /**
         * 音频帧文件
         */
        for (int i = 0; i < length; i++) {
            originaldata[i] = buffer[i + 44];
        }
    }



}
