/*
 * Copyright 2014 Frakbot (Sebastiano Poggi and Francesco Pontillo)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.juttec.goldmetal.customview.listview;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Provides "jumping beans" functionality for a TextView.
 * <p/>
 * Remember to call the {@link #stopJumping()} method once you're done
 * using the JumpingBeans (that is, when you detach the TextView from
 * the view tree, you hide it, or the parent Activity/Fragment goes in
 * the paused status). This will allow to release the animations and
 * free up memory and CPU that would be otherwise wasted.
 * <p/>
 * Please note that you:
 * <ul>
 * <li><b>Must not</b> try to change a jumping beans text in a textview before calling
 * {@link #stopJumping()} as to avoid unnecessary invalidation calls;
 * the JumpingBeans class cannot know when this happens and will keep
 * animating the textview (well, try to, anyway), wasting resources</li>
 * <li><b>Must not</b> try to use a jumping beans text in another view; it will not
 * animate. Just create another jumping beans animation for each new
 * view</li>
 * <li><b>Must not</b> use more than one JumpingBeans instance on a single TextView, as
 * the first cleanup operation called on any of these JumpingBeans will also cleanup
 * all other JumpingBeans' stuff. This is most likely not what you want to happen in
 * some cases.</li>
 * <li><b>Should not</b> use JumpingBeans on large chunks of text. Ideally this should
 * be done on small views with just a few words. We've strived to make it as inexpensive
 * as possible to use JumpingBeans but invalidating and possibly relayouting a large
 * TextView can be pretty expensive.</li>
 * </ul>
 */
public final class JumpingBeans {

    /**
     * The default fraction of the whole animation time spent actually animating.
     * The rest of the range will be spent in "resting" state.
     * This the "duty cycle" of the jumping animation.
     */
    public static final float DEFAULT_ANIMATION_DUTY_CYCLE = 0.5f;

    /**
     * The default duration of a whole jumping animation loop, in milliseconds.
     */
    public static final int DEFAULT_LOOP_DURATION = 1500;

    private JumpingBeansSpan[] jumpingBeans;
    private WeakReference<TextView> textView;

    private JumpingBeans(JumpingBeansSpan[] beans, TextView textView) {
        // Clients will have to use the builder
        this.jumpingBeans = beans;
        this.textView = new WeakReference<TextView>(textView);
    }

    /**
     * Stops the jumping animation and frees up the animations.
     */
    public void stopJumping() {
        for (JumpingBeansSpan bean : jumpingBeans) {
            if (bean != null) {
                bean.teardown();
            }
        }

        TextView tv = textView.get();
        if (tv != null) {
            CharSequence text = tv.getText();
            if (text instanceof Spanned) {
                CharSequence cleanText = removeJumpingBeansSpans((Spanned) text);
                tv.setText(cleanText);
            }
        }
    }

    private static CharSequence removeJumpingBeansSpans(Spanned text) {
        SpannableStringBuilder sbb = new SpannableStringBuilder(text.toString());
        Object[] spans = text.getSpans(0, text.length(), Object.class);
        for (Object span : spans) {
            if (!(span instanceof JumpingBeansSpan)) {
                sbb.setSpan(span, text.getSpanStart(span),
                        text.getSpanEnd(span), text.getSpanFlags(span));
            }
        }
        return sbb;
    }


    public static class Builder {

        private int startPos, endPos;
        private float animRange = DEFAULT_ANIMATION_DUTY_CYCLE;
        private int loopDuration = DEFAULT_LOOP_DURATION;
        private int waveCharDelay = -1;
        private CharSequence text;
        private TextView textView;
        private boolean wave;


        public Builder appendJumpingDots(TextView textView) {
            if (textView == null) {
                throw new NullPointerException("The textView must not be null");
            }

            CharSequence text = !TextUtils.isEmpty(textView.getText()) ? textView.getText() : "";
            if (text.length() > 0 && text.subSequence(text.length() - 1, text.length()).equals("…")) {
                text = text.subSequence(0, text.length() - 1);
            }

            if (text.length() < 3 || !TextUtils.equals(text.subSequence(text.length() - 3, text.length()), "...")) {
                text = new SpannableStringBuilder(text).append("...");  // Preserve spans in original text
            }

            this.text = text;
            this.wave = true;
            this.textView = textView;
            this.startPos = this.text.length() - 3;
            this.endPos = this.text.length();
            return this;
        }


        public Builder makeTextJump(TextView textView, int startPos, int endPos) {
            if (textView == null || textView.getText() == null) {
                throw new NullPointerException("The textView and its text must not be null");
            }

            if (endPos < startPos) {
                throw new IllegalArgumentException("The start position must be smaller than the end position");
            }

            if (startPos < 0) {
                throw new IndexOutOfBoundsException("The start position must be non-negative");
            }

            this.text = textView.getText();
            if (endPos > text.length()) {
                throw new IndexOutOfBoundsException("The end position must be smaller than the text length");
            }

            this.wave = true;
            this.textView = textView;
            this.startPos = startPos;
            this.endPos = endPos;
            return this;
        }


        public Builder setAnimatedDutyCycle(float animatedRange) {
            if (animatedRange <= 0f || animatedRange > 1f) {
                throw new IllegalArgumentException("The animated range must be in the (0, 1] range");
            }
            this.animRange = animatedRange;
            return this;
        }


        public Builder setLoopDuration(int loopDuration) {
            if (loopDuration < 1) {
                throw new IllegalArgumentException("The loop duration must be bigger than zero");
            }
            this.loopDuration = loopDuration;
            return this;
        }


        public Builder setWavePerCharDelay(int waveCharOffset) {
            if (waveCharOffset < 0) {
                throw new IllegalArgumentException("The wave char offset must be non-negative");
            }
            this.waveCharDelay = waveCharOffset;
            return this;
        }


        public Builder setIsWave(boolean wave) {
            this.wave = wave;
            return this;
        }


        public JumpingBeans build() {
            SpannableStringBuilder sbb = new SpannableStringBuilder(text);
            JumpingBeansSpan[] jumpingBeans;
            if (!wave) {
                jumpingBeans = new JumpingBeansSpan[]{new JumpingBeansSpan(textView, loopDuration, 0, 0, animRange)};
                sbb.setSpan(jumpingBeans[0], startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                if (waveCharDelay == -1) {
                    waveCharDelay = loopDuration / (3 * (endPos - startPos));
                }

                jumpingBeans = new JumpingBeansSpan[endPos - startPos];
                for (int pos = startPos; pos < endPos; pos++) {
                    JumpingBeansSpan jumpingBean =
                            new JumpingBeansSpan(textView, loopDuration, pos - startPos, waveCharDelay, animRange);
                    sbb.setSpan(jumpingBean, pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    jumpingBeans[pos - startPos] = jumpingBean;
                }
            }

            textView.setText(sbb);
            return new JumpingBeans(jumpingBeans, textView);
        }
    }
}
