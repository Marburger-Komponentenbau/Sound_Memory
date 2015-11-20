package com.conetex.somemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.view.Display;
import android.content.res.Configuration;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Random;
import java.util.StringTokenizer;

import static android.graphics.Color.*;

public class Activity_Main extends Activity { // ActionBarActivity

    final static int DISPLAY_MENU_HIGH_DP = 32;

    final static int NEW_GAME_IMAGE = R.drawable.a_new_game;

    final static int[] DECK_SIZES = {
          3
        , 5
        , 6
        , 7
        , 8
        , 9
        , 10
    };

    final static Integer[] DECK_SIZE_IMAGES = {
          R.drawable.a_decksize_3
        , R.drawable.a_decksize_5
        , R.drawable.a_decksize_8
        , R.drawable.a_decksize_8
        , R.drawable.a_decksize_8
        , R.drawable.a_decksize_8
        , R.drawable.a_decksize_8
    };

    final static String[] DECK_SIZE_STRINGS = {
          "6"
        , "10"
        , "12"
        , "14"
        , "16"
        , "18"
        , "20"
    };

    final static String[] DECK_SIZE_SUBSTRINGS = {
          "cards"
        , "cards"
        , "cards"
        , "cards"
        , "cards"
        , "cards"
        , "cards"
    };


    final static Integer[] DECK_IMAGES = {
          R.drawable.a_deck_birds
        , R.drawable.a_deck_farm
    };

    final static int CARD_PLACEHOLDER_IMAGE = R.drawable.imagex;
    final static int CARD_BACK_IMAGE = R.drawable.card_back;

    private static int[][] MEDIA_IDS = {
        {
              R.drawable.image0, R.raw.winsound
            , R.drawable.image1, R.raw.winsound
            , R.drawable.image2, R.raw.winsound
            , R.drawable.image3, R.raw.winsound
            , R.drawable.image4, R.raw.winsound
            , R.drawable.image5, R.raw.winsound
            , R.drawable.image6, R.raw.winsound
            , R.drawable.image7, R.raw.winsound
            , R.drawable.image8, R.raw.winsound
            , R.drawable.image9, R.raw.winsound
            , R.drawable.imagea, R.raw.winsound
            , R.drawable.imageb, R.raw.winsound
            , R.drawable.imagec, R.raw.winsound
            , R.drawable.imaged, R.raw.winsound
            , R.drawable.imagee, R.raw.winsound
            , R.drawable.imagef, R.raw.winsound
        }
      , {
              R.drawable.image0, R.raw.winsound
            , R.drawable.image1, R.raw.winsound
            , R.drawable.image2, R.raw.winsound
            , R.drawable.image3, R.raw.winsound
            , R.drawable.image4, R.raw.winsound
            , R.drawable.image5, R.raw.winsound
            , R.drawable.image6, R.raw.winsound
            , R.drawable.image7, R.raw.winsound
            , R.drawable.image8, R.raw.winsound
            , R.drawable.image9, R.raw.winsound
            , R.drawable.imagea, R.raw.winsound
            , R.drawable.imageb, R.raw.winsound
            , R.drawable.imagec, R.raw.winsound
            , R.drawable.imaged, R.raw.winsound
            , R.drawable.imagee, R.raw.winsound
            , R.drawable.imagef, R.raw.winsound
        }
    };

    protected static class Card {
        protected static final int COVERED = 0;
        protected static final int COVERING = 1;
        protected static final int EXPOSED = 2;
        protected static final int SOLVED = 3;
        protected static final int REPLACEMENT = 4;
        private int state;
        private int frontImageId;
        private int soundId;
        private MediaPlayer mediaPlayer;
        private ImageView imageView;
        private int playerPosition;
        Card(int frontImageId, int soundId, int state, Activity_Main context, int playerPosition){
            //this.mediaId = mediaId;
            this.frontImageId = frontImageId;
            this.soundId = soundId;
            this.state = state;
            this.mediaPlayer = MediaPlayer.create(context, soundId);
            this.playerPosition = 0;
            if(this.mediaPlayer != null) {
                this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Card.this.playerPosition = 0;//mp.getCurrentPosition();
                    }
                });
                if (playerPosition > 0) {
                    this.mediaPlayer.seekTo(playerPosition);
                    this.mediaPlayer.start();
                }
            }
            //Resources r = context.getResources();
            //String p = context.getPackageName();
            //this.frontImageId = r.getIdentifier("image" + Character.toString( mediaId ), "drawable", p);
        }
        Card(){
            //this.mediaId = ' ';
            this.frontImageId = CARD_PLACEHOLDER_IMAGE;
            this.soundId = 0;
            this.state = REPLACEMENT;
            this.mediaPlayer = null;
        }
        public int getFrontImageId(){
            return this.frontImageId;
        }
        public int getBackImageId(){
            if(this.state == Card.REPLACEMENT){
                return CARD_PLACEHOLDER_IMAGE;
            }
            //return this.frontImageId;// TODO das ist nur für Test... eigentlich heißt es ""
            return CARD_BACK_IMAGE;
        }
        protected ImageView getImageView() {
            return imageView;
        }
        protected void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
    }

    protected class Table {
        private int cardSize;
        private Card[][] cards;
        private Card exposedCard;
        private int solvedPairsCount;
        private int pairCount;
        private int shortEdgeSpots;
        private int longEdgeSpots;

        private Table(int cardSize, Card[][] cards, int pairCount){
            this.cardSize = cardSize;
            this.shortEdgeSpots = cards.length;
            this.longEdgeSpots = cards[0].length;
            this.cards = cards;
            this.pairCount = pairCount;

            this.solvedPairsCount = 0;
            this.exposedCard = null;
            boolean coverExposed = false;
            for (Card[] row : this.cards) {
                for (Card card : row) {
                    if(card.state == Card.EXPOSED){
                        if(this.exposedCard == null){
                            this.exposedCard = card;
                        }
                        else{
                            card.state = Card.COVERED;
                            coverExposed = true;
                        }
                    }
                    else if(card.state == Card.SOLVED){
                        this.solvedPairsCount++;
                    }
                }
            }
            if(coverExposed){
                this.exposedCard.state = Card.COVERED;
                this.exposedCard = null;
            }
            this.solvedPairsCount = this.solvedPairsCount / 2;
        }

        protected int getShortEdgeSpots(){
            return this.cards.length;
        }

        protected int getLongEdgeSpots(){
            return this.cards[0].length;
        }

        public void solv() {
            this.solv(exposedCard);
            this.exposedCard = null;
            this.solvedPairsCount++;
        }

        public void solv(Card c) {
            if(c.state != Card.EXPOSED){
                ImageView ivC = c.getImageView();
                if(ivC != null){
                    Activity_Main.this.imageViewAnimatedChange( ivC, c.getFrontImageId() );
                }
            }
            c.state = Card.SOLVED;
        }

        public void coverNow() {
            this.coverNow(this.exposedCard);
            this.exposedCard = null;
        }

        public void coverNow(Card c) {
            c.state = Card.COVERED;
            ImageView ivC = c.getImageView();
            if(ivC != null){
                Activity_Main.this.imageViewAnimatedChange( ivC, c.getBackImageId() );
            }
        }

        public void expose(Card c) {
            c.state = Card.EXPOSED;
            this.exposedCard = c;
            ImageView ivC = c.getImageView();
            if(ivC != null){
                Activity_Main.this.imageViewAnimatedChange( ivC, c.getFrontImageId() );
            }
        }

        public void exposeAndCoverLater(Card c) {
            c.state = Card.COVERING;
            ImageView ivC = c.getImageView();
            if(ivC != null){
                Activity_Main.this.imageViewAnimatedChange( ivC, c.getFrontImageId() );
            }
            coverLater(c);
        }

        public void coverLater() {
            this.coverLater(this.exposedCard);
            this.exposedCard = null;
        }

        public void coverLater(final Card a) {
            a.state = Card.COVERING;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(7500);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final ImageView ivA = a.getImageView();
                    ivA.post(new Runnable() {
                        public void run() {
                            if(a.state == Card.COVERING) {
                                a.state = Card.COVERED;
                                //ivA.setImageResource(a.getBackImageId());
                                Activity_Main.this.imageViewAnimatedChange( ivA, a.getBackImageId() );
                            }
                        }
                    });
                }
            }).start();
        }

    }

    static abstract class ViewAspect {
        protected static final int NORTH = 0;
        protected static final int EAST = 1;
        protected static final int SOUTH = 2;
        protected static final int WEST = 3;

        protected abstract int getMaxX(Table t);
        protected abstract int getMaxY(Table t);
        protected abstract Card getCard(Table t, int x, int y);

        protected abstract int getWidthPx(Activity_Main ctx);
        protected abstract int getHeightPx(Activity_Main ctx);

        protected abstract LinearLayout.LayoutParams getMenuLayoutParams(Activity_Main ctx);
        protected abstract void setMenuOrientation(LinearLayout layout);
        protected abstract void setMainOrientation(LinearLayout layout);

        protected abstract void removeTableView(Activity_Main ctx);

        protected abstract void createTableView(Activity_Main ctx);

        protected abstract void removeMenuView(Activity_Main ctx);

        protected abstract void createMenuView(Activity_Main ctx);

        protected void createTableMenuView(Activity_Main ctx) {
            this.setMainOrientation(ctx.layout);
            createMenuView(ctx);
            createTableView(ctx);
        }

        protected LinearLayout makeMenuView(final Activity_Main ctx) {
            LinearLayout iv = new LinearLayout(ctx);
            this.setMenuOrientation(iv);

            LinearLayout.LayoutParams llp = this.getMenuLayoutParams(ctx);
            iv.setLayoutParams(llp);
            iv.setBackgroundColor(Color.MAGENTA);

/*
        ImageView tbtShowImages = new ImageView(this);
        tbtShowImages.setImageResource(R.drawable.a_show_images_on);
        iv.addView(tbtShowImages);

        TextView tvInfo = new TextView(this);
        tvInfo.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Small);
        tvInfo.setText("Amsel");
        iv.addView(tvInfo);
*/

            final Spinner sDeck = new Spinner(ctx);
            SimpleImageArrayAdapter adapter = new SimpleImageArrayAdapter(ctx,
                    Activity_Main.DECK_IMAGES
            );
            sDeck.setAdapter(adapter);
            sDeck.setSelection(0);
            iv.addView(sDeck);


            final Spinner sDeckSize = new Spinner(ctx);
            SimpleTextImageArrayAdapter adapter2 = new SimpleTextImageArrayAdapter(
                      ctx
                    , Activity_Main.DECK_SIZE_IMAGES
                    , Activity_Main.DECK_SIZE_STRINGS
                    , Activity_Main.DECK_SIZE_SUBSTRINGS
            );
            sDeckSize.setAdapter(adapter2);
            sDeckSize.setSelection(0);
            iv.addView(sDeckSize);

            ImageView btNewGame = new ImageView(ctx);
            btNewGame.setImageResource(Activity_Main.NEW_GAME_IMAGE);
            iv.addView(btNewGame);


            btNewGame.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ctx.table = null;
                            ctx.pairCount = DECK_SIZES[sDeckSize.getSelectedItemPosition()];
                            ctx.deckId = sDeck.getSelectedItemPosition();
                            Table t = ctx.createNewTable( ctx.pairCount, Activity_Main.MEDIA_IDS[ctx.deckId] );
                            ctx.switchTable(t);
                        }
                    }
            );

            return iv;

/*
        String[] arraySpinner = new String[] {
                "1", "2", "3", "4", "5"
        };
        Spinner s =  new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);
        iv.addView(s);
*/
            ///////////
/*
        ImageButton ib = new ImageButton(this);
        ib.setImageResource(R.drawable.image1);
        iv.addView(ib);
*/
        }

        protected LinearLayout makeTableView(Table t, Activity_Main ctx) {

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            llp.gravity= Gravity.CENTER;

            LinearLayout layoutRow = new LinearLayout(ctx);
            layoutRow.setLayoutParams( llp );
            //layoutRow.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            layoutRow.setOrientation(LinearLayout.VERTICAL);
            layoutRow.setBackgroundColor(YELLOW);
            int x = this.getMaxX(t);
            int y = this.getMaxY(t);
            for(int iy = 0; iy < y; iy++) {
                LinearLayout layoutCol = new LinearLayout(ctx);
                layoutCol.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                layoutCol.setOrientation(LinearLayout.HORIZONTAL);
                for(int ix = 0; ix < x; ix++) {
                    Card res = this.getCard(t, ix, iy);
                    layoutCol.addView(this.makeImageView(t.cardSize, res, ctx));
                }
                layoutRow.addView( layoutCol );
            }
            return layoutRow;
        }

        private View makeImageView(int fieldSize, final Card card, final Activity_Main ctx) {
            ImageView iv = new ImageView(ctx);
            //MyView iv = new MyView(this);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(fieldSize, fieldSize);

            int xCount = this.getMaxX(ctx.table);
            int marginLeftRight = (this.getWidthPx(ctx) - xCount * fieldSize) / (xCount * 2);
            int yCount = this.getMaxY(ctx.table);
            int marginTopBottom = (this.getHeightPx(ctx) - yCount * fieldSize) / (yCount * 2);

            llp.setMargins(marginLeftRight, marginTopBottom, marginLeftRight, marginTopBottom);

            iv.setLayoutParams(llp);
            //iv.setMaxWidth(fieldSize);
            //iv.setMaxHeight(fieldSize);

/*
        ObjectAnimator animX = ObjectAnimator.ofFloat(iv, "x", 50f);
        ObjectAnimator animY = ObjectAnimator.ofFloat(iv, "y", 100f);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        animSetXY.start();
*/

            iv.setBackgroundColor(RED);

            int backImageID = card.getBackImageId();// TODO nur fuer debug

            iv.setImageResource(card.getBackImageId());

            iv.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int[] lo = new int[2];
                            v.getLocationOnScreen(lo);
                            if(card.state == Card.COVERED || card.state == Card.COVERING){
                                if(ctx.table.exposedCard == null){
                                    ctx.table.expose(card);
                                }
                                else {
                                    if(card.frontImageId == ctx.table.exposedCard.frontImageId){
                                        ctx.table.solv();
                                        ctx.table.solv(card);
                                        if( ctx.table.solvedPairsCount >= ctx.table.pairCount ){
                                            // TODO solved all ...
                                        }
                                    }
                                    else{
                                        ctx.table.coverLater();
                                        ctx.table.exposeAndCoverLater(card);
                                    }
                                }
                                //Activity_Main.this.imageViewAnimatedChange( (ImageView)v, r.getFrontImageId() );
                                ctx.animatePuls(lo[0], lo[1], v.getWidth());
                                // TODO play sound
                                if(card.mediaPlayer != null) {
                                    card.mediaPlayer.start();
                                }
                            }
                            else if(card.state == Card.EXPOSED){
                                if(card == ctx.table.exposedCard) {
                                    ctx.table.coverNow();
                                }
                                else{
                                    ctx.table.coverNow(card);
                                }
                            }
                            else if(card.state == Card.SOLVED){
                                ctx.animatePuls(lo[0], lo[1], v.getWidth());
                                // TODO play sound
                                if(card.mediaPlayer != null) {
                                    card.mediaPlayer.start();
                                }
                            }
                        }
                    }
            );
            card.setImageView(iv);
            return iv;
        }
    }

    static abstract class AspectNorthSouth extends ViewAspect {
        protected int getMaxX(Table t){
            return t.shortEdgeSpots;
        }
        protected int getMaxY(Table t){
            return t.longEdgeSpots;
        }
        protected int getWidthPx(Activity_Main ctx){
            return ctx.displayShortPx;
        }
        protected int getHeightPx(Activity_Main ctx){
            return ctx.displayLongPx;
        }
        protected LinearLayout.LayoutParams getMenuLayoutParams(Activity_Main ctx) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ctx.displayMenuHighPx);
            return llp;
        }
        protected void setMenuOrientation(LinearLayout menuLayout) {
            menuLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
        protected void setMainOrientation(LinearLayout layout) {
            layout.setOrientation(LinearLayout.VERTICAL);
        }
    }

    static abstract class AspectEastWest extends ViewAspect {
        protected int getMaxX(Table t){
            return t.longEdgeSpots;
        }
        protected int getMaxY(Table t){
            return t.shortEdgeSpots;
        }
        protected int getWidthPx(Activity_Main ctx){
            return ctx.displayLongPx;
        }
        protected int getHeightPx(Activity_Main ctx){
            return ctx.displayShortPx;
        }
        protected LinearLayout.LayoutParams getMenuLayoutParams(Activity_Main ctx) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ctx.displayMenuHighPx, LinearLayout.LayoutParams.MATCH_PARENT);
            return llp;
        }
        protected void setMenuOrientation(LinearLayout menuLayout) {
            menuLayout.setOrientation(LinearLayout.VERTICAL);
        }
        protected void setMainOrientation(LinearLayout layout) {
            layout.setOrientation(LinearLayout.HORIZONTAL);
        }
    }

    static class AspectNorth extends AspectNorthSouth{
        protected void removeTableView(Activity_Main ctx) {
            ctx.layout.removeViewAt( ctx.layout.getChildCount()-1  );
        }
        protected void createTableView(Activity_Main ctx) {
            ctx.layout.addView( this.makeTableView(ctx.table, ctx), ctx.layout.getChildCount() );
        }
        protected void removeMenuView(Activity_Main ctx) {
            ctx.layout.removeViewAt( 0 );
        }
        protected void createMenuView(Activity_Main ctx) {
            ctx.layout.addView( this.makeMenuView(ctx), 0 );
        }
        protected Card getCard(Table t, int x, int y){
            return t.cards[x][y];
        }
    }

    static class AspectEast extends AspectEastWest{
        protected void removeTableView(Activity_Main ctx) {
            ctx.layout.removeViewAt( 0 );
        }
        protected void createTableView(Activity_Main ctx) {
            ctx.layout.addView( this.makeTableView(ctx.table, ctx), 0 );
        }
        protected void removeMenuView(Activity_Main ctx) {
            ctx.layout.removeViewAt( ctx.layout.getChildCount()-1 );
        }
        protected void createMenuView(Activity_Main ctx) {
            ctx.layout.addView( this.makeMenuView(ctx), ctx.layout.getChildCount() );
        }
        protected Card getCard(Table t, int x, int y){
            return t.cards[y][t.longEdgeSpots - 1 - x];
        }
        protected int getMenuOrientation(){
            return ViewAspect.EAST;
        }
    }

    static class AspectSouth extends AspectNorthSouth{
        protected void removeTableView(Activity_Main ctx) {
            ctx.layout.removeViewAt( 0 );
        }
        protected void createTableView(Activity_Main ctx) {
            ctx.layout.addView( this.makeTableView(ctx.table, ctx), 0 );
        }
        protected void removeMenuView(Activity_Main ctx) {
            ctx.layout.removeViewAt( ctx.layout.getChildCount()-1 );
        }
        protected void createMenuView(Activity_Main ctx) {
            ctx.layout.addView( this.makeMenuView(ctx), ctx.layout.getChildCount() );
        }
        protected Card getCard(Table t, int x, int y){
            return t.cards[t.shortEdgeSpots - 1 - x][t.longEdgeSpots - 1 - y];
        }
    }

    static class AspectWest extends AspectEastWest{
        protected void removeTableView(Activity_Main ctx) {
            ctx.layout.removeViewAt( ctx.layout.getChildCount()-1  );
        }
        protected void createTableView(Activity_Main ctx) {
            ctx.layout.addView( this.makeTableView(ctx.table, ctx), ctx.layout.getChildCount() );
        }
        protected void removeMenuView(Activity_Main ctx) {
            ctx.layout.removeViewAt( 0 );
        }
        protected void createMenuView(Activity_Main ctx) {
            ctx.layout.addView( this.makeMenuView(ctx), 0 );
        }
        protected Card getCard(Table t, int x, int y){
            return t.cards[t.shortEdgeSpots - 1 - y][x];
        }
    }

    protected static final String DELIMITER = ";";
    protected static final String TABLE_SIZE = "table_size";
    protected static final String CARD_STATES = "card_states";
    protected static final String DECK_STATE = "deck_state";

    private int deckId = 0;
    private int pairCount = DECK_SIZES[0];

    private Table table = null;

    double displayRatio = 1;
    int displayShortPx = 200;
    int displayLongPx = 200;
    int displayMenuHighPx = 24;

    private ViewAspect viewAspect = null;
    private AspectNorth aspectNorth = new AspectNorth();
    private AspectEast aspectEast = new AspectEast();
    private AspectSouth aspectSouth = new AspectSouth();
    private AspectWest aspectWest = new AspectWest();

    private LinearLayout layout = null;

    private View pulsView = null;

    public Activity_Main(){
    }

    protected void initOrientationAspect(Configuration config, Display di){
        this.viewAspect = this.getOrientationAspect(config, di);
    }

    protected ViewAspect getOrientationAspect(Configuration config, Display di){
        int r = di.getRotation();
            /* TODO: check about orientation
                  (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                  (config.orientation == Configuration.ORIENTATION_PORTRAIT)
            */
        if (config.screenHeightDp < config.screenWidthDp) {
            // East or West
            if(r == Surface.ROTATION_0){        // North but Landscape
                // East
                return this.aspectEast;
            }
            else if(r == Surface.ROTATION_90){  // East
                // East
                return this.aspectEast;
            }
            else if(r == Surface.ROTATION_180){ // South but Landscape
                // West
                return this.aspectWest;
            }
            else if(r == Surface.ROTATION_270){ // West
                // West
                return this.aspectWest;
            }
            else{                               // no rotation
                // East
                return this.aspectEast;
            }
        }
        else{
            // North or South
            if(r == Surface.ROTATION_0){        // North
                // North
                return this.aspectNorth;
            }
            else if(r == Surface.ROTATION_90){  // East but Landscape
                // North
                return this.aspectNorth;
            }
            else if(r == Surface.ROTATION_180){ // South
                // South
                return this.aspectSouth;
            }
            else if(r == Surface.ROTATION_270){ // West but Landscape
                // South
                return this.aspectSouth;
            }
            else{                               // no rotation
                // North
                return this.aspectNorth;
            }
        }
    }

    private void initDisplayMetrics(Configuration config, Display di) {
        DisplayMetrics metrics = new DisplayMetrics();
        di.getMetrics(metrics);
        //Resources r = getResources();
        //DisplayMetrics metrics = r.getDisplayMetrics();

        if (metrics.heightPixels < metrics.widthPixels) {
            this.displayShortPx = metrics.heightPixels;
            this.displayLongPx = metrics.widthPixels;
        } else {
            this.displayShortPx = metrics.widthPixels;
            this.displayLongPx = metrics.heightPixels;
        }

        // px = dp * (dpi / 160).
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DISPLAY_MENU_HIGH_DP, metrics);
        this.displayMenuHighPx = (int) px;

        this.displayLongPx = this.displayLongPx - this.displayMenuHighPx;

        this.displayRatio = (double) this.displayLongPx / (double) this.displayShortPx; // high / Width
    }

    private Table createNewTable(int pairCount, int[] mediaIds) {

        if(mediaIds.length < pairCount){
            pairCount = mediaIds.length;
        }

        int usedSpotCount = pairCount * 2;
        double sh_d = Math.sqrt(usedSpotCount / this.displayRatio);
        double lo_d = usedSpotCount / sh_d;
        int lo = (int) lo_d;
        int sh = (int) sh_d;
        while (sh * lo < usedSpotCount) {
            if (sh * ++lo < usedSpotCount) {
                if (++sh * --lo < usedSpotCount) {
                    lo++;
                }
            }
        }
        int sizeShort = this.displayShortPx / sh;
        int sizeLong = this.displayLongPx / lo;
        int fieldSize = sizeShort < sizeLong ? sizeShort : sizeLong;

        Resources r = this.getResources();
        String p = this.getPackageName();

        Random rand = new Random();

        //int[] mediaIds = Activity_Main.MEDIA_IDS;
        int deckMaxIdx = mediaIds.length / 2 - 1;

        int totalSpotCount = sh * lo;
        Card[] urn = new Card[totalSpotCount];
        int lotMax = 0;
        char imageId = 0;
        while(lotMax < usedSpotCount){
            int lotIdx = rand.nextInt(deckMaxIdx);
            urn[lotMax++] = new Card( mediaIds[lotIdx*2], mediaIds[lotIdx*2+1], Card.COVERED, this, 0 );
            urn[lotMax++] = new Card( mediaIds[lotIdx*2], mediaIds[lotIdx*2+1], Card.COVERED, this, 0 );
            mediaIds[lotIdx*2] = mediaIds[deckMaxIdx*2];
            mediaIds[lotIdx*2+1] = mediaIds[deckMaxIdx*2+1];
            if(--deckMaxIdx == 0){
                urn[lotMax++] = new Card( mediaIds[0], mediaIds[1], Card.COVERED, this, 0 );
                urn[lotMax++] = new Card( mediaIds[0], mediaIds[1], Card.COVERED, this, 0 );
                break;
            }
        }
        while(lotMax < totalSpotCount){
            Card curRes = new Card();
            urn[lotMax++] = curRes;
        }
        lotMax--;

        Card[][] spots = new Card[sh][lo];
        int ish = 0, ilo = 0;
        loop:
        while(ish < sh){
            while(ilo < lo){
                if(lotMax == 0){
                    spots[ish][ilo] = urn[0];
                    break loop;
                }
                int randomIdx = rand.nextInt(lotMax);
                spots[ish][ilo++] = urn[randomIdx];
                urn[randomIdx] = urn[lotMax--];
            }
            ilo = 0;
            ish++;
        }
        return new Table(fieldSize, spots, pairCount);
    }

    private Table createSavedTable(int pairCount, int[] mediaIds, Bundle savedInstanceState){
        String sizes = savedInstanceState.getString(TABLE_SIZE);
        int[] card_states = savedInstanceState.getIntArray(CARD_STATES);
        /*
        int[] card_images = savedInstanceState.getIntArray(CARD_IMAGES);
        int[] card_sounds = savedInstanceState.getIntArray(CARD_SOUNDS);
        int[] card_sound_positions = savedInstanceState.getIntArray(CARD_SOUND_POS);
        */
        if(sizes.length() > 4) {
            StringTokenizer sizeTokens = new StringTokenizer(sizes, DELIMITER);
            int cardSize = Integer.parseInt(sizeTokens.nextToken());
            int shortEdgeSpots = Integer.parseInt(sizeTokens.nextToken());
            int longEdgeSpots = Integer.parseInt(sizeTokens.nextToken());
            if( card_states.length == shortEdgeSpots * longEdgeSpots * 4 ){
                Card[][] cards = new Card[shortEdgeSpots][longEdgeSpots];
                int i = 0, x = 0;
                for (Card[] row : this.table.cards) {
                    int y = 0;
                    for (Card card : row) {
                        int frontImageId   = card_states[i++];
                        int soundId        = card_states[i++];
                        int state          = card_states[i++];
                        int playerPosition = card_states[i++];
                        cards[x][y++] = new Card(frontImageId, soundId, state, this, playerPosition);
                    }
                    x++;
                }
                return new Table(cardSize, cards, pairCount);
            }
        }
        return this.createNewTable(pairCount, mediaIds);
    }


    protected void getStates(int[] card_states){
        if( card_states.length == this.table.cards.length * this.table.cards[0].length * 4 ){
            int i = 0;
            for (Card[] row : this.table.cards) {
                for (Card card : row) {
                    card_states[i++] = card.frontImageId;
                    card_states[i++] = card.soundId;
                    card_states[i++] = card.state;
                    card_states[i++] = card.playerPosition;
                }
            }
        }
    }

    static public class SimpleTextImageArrayAdapter extends ArrayAdapter<Integer> {
        private Integer[] images;
        private String[] texts;
        private String[] subTexts;
        private Activity_Main context;

        public SimpleTextImageArrayAdapter(Activity_Main context, Integer[] images, String[] texts, String[] subTexts) {
            super(context, android.R.layout.simple_spinner_item, images);
            this.images = images;
            this.texts = texts;
            this.subTexts = subTexts;
            this.context = context;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCView(position, convertView, parent);
        }

        private View getCView(int position, View convertView, ViewGroup parent) {
            //return getImageForPosition(position);

            LayoutInflater inflater = this.context.getLayoutInflater();
            View row=inflater.inflate(R.layout.row, parent, false);

            TextView label=(TextView)row.findViewById(R.id.company);
            label.setText(this.texts[position]);

            TextView sub=(TextView)row.findViewById(R.id.sub);
            sub.setText(this.subTexts[position]);

            ImageView icon=(ImageView)row.findViewById(R.id.image);
            icon.setImageResource(this.images[position]);

            return row;
        }

    }

    static public class SimpleImageArrayAdapter extends ArrayAdapter<Integer> {
        private Integer[] images;

        public SimpleImageArrayAdapter(Context context, Integer[] images) {
            super(context, android.R.layout.simple_spinner_item, images);
            this.images = images;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getImageForPosition(position);
            //return getCView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getImageForPosition(position);
            //return getCView(position, convertView, parent);
        }

        private View getImageForPosition(int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(images[position]);
            imageView.setLayoutParams(
                    new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            return imageView;
        }

    }

    public void switchTable(Table t) {
        this.viewAspect.removeTableView(this);
        this.table = t;
        this.viewAspect.createTableView(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        WindowManager wm =  this.getWindowManager();
        Display di = wm.getDefaultDisplay();
        ViewAspect actualAspect = this.getOrientationAspect(newConfig, di);

        if(this.viewAspect != actualAspect){
            this.viewAspect.removeMenuView(this);
            this.viewAspect.removeTableView(this);
            this.viewAspect = actualAspect;
            this.viewAspect.createTableMenuView(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        if(this.table != null){
            String size = "";
            size += this.table.cardSize;
            size += DELIMITER;
            size += this.table.getShortEdgeSpots();
            size += DELIMITER;
            size += this.table.getLongEdgeSpots();
            savedInstanceState.putString( TABLE_SIZE, size );

            int[] deckState = new int[2];
            deckState[0] = this.deckId;
            deckState[1] = this.pairCount;
            savedInstanceState.putIntArray( DECK_STATE, deckState );

            int s = this.table.getShortEdgeSpots() * this.table.getLongEdgeSpots() * 3;
            int[] card_states = new int[s];
            this.getStates(card_states);
            savedInstanceState.putIntArray( CARD_STATES, card_states );
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first

        // TODO save current state
        // Save the note's current draft, because the activity is stopping
        // and we want to be sure the current note progress isn't lost.
        ContentValues values = new ContentValues();
        values.put("key", "value");
        values.put("key", "value2");
 
        /* TODO wie speichert man was?
        getContentResolver().update(
                new Uri(),    // The URI for the note to update.
                values,  // The map of column names and new values to apply to them.
                null,    // No SELECT criteria are used.
                null     // No WHERE columns are used.
        );
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration config = getResources().getConfiguration();
        //WindowManager wm = (WindowManager) ( this.getSystemService(Activity_Main.WINDOW_SERVICE) );
        WindowManager wm =  this.getWindowManager();
        Display di = wm.getDefaultDisplay();


        this.initOrientationAspect(config, di);
        this.initDisplayMetrics(config, di);

        if (savedInstanceState != null) {
            int[] deckState = savedInstanceState.getIntArray(DECK_STATE);
            if(deckState.length == 2) {
                this.deckId = deckState[0];
                this.pairCount = deckState[1];
            }
            this.table = this.createSavedTable(this.pairCount, Activity_Main.MEDIA_IDS[this.deckId], savedInstanceState);
        }
        else{
            this.table = this.createNewTable(this.pairCount, Activity_Main.MEDIA_IDS[this.deckId]);
        }

        /*
        LinearLayout ll = new LinearLayout( this );
        ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        ll.setMainOrientation(LinearLayout.VERTICAL);
        */

        this.layout = new LinearLayout(this);
        this.layout.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.layout.setBackgroundColor(GREEN);

        this.viewAspect.createTableMenuView(this);

        setContentView(this.layout);
/*
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.puls);
        this.pulsView = iv;//new MyView(this);
        this.pulsView.setVisibility(View.INVISIBLE);
        this.addContentView(this.pulsView, new FrameLayout.LayoutParams(120, 120));
*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        // Activity being restarted from stopped state
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        // The activity is either being restarted or started for the first time

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        for (Card[] row : this.table.cards) {
            for (Card card : row) {
                if(card.mediaPlayer != null && card.playerPosition > 0 && !card.mediaPlayer.isPlaying() ){
                    card.mediaPlayer.start();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        for (Card[] row : this.table.cards) {
            for (Card card : row) {
                if(card.mediaPlayer != null && card.mediaPlayer.isPlaying()){
                    card.mediaPlayer.pause();
                    card.playerPosition = card.mediaPlayer.getCurrentPosition();
                }
            }
        }
    }

    public class _MyView extends View {
        public _MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            int x = getWidth();
            int y = getHeight();
            int radius;
            radius = 60;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            //paint.setAlpha(1);
            canvas.drawPaint(paint);
            // Use Color.parseColor to define HTML colors
            paint.setColor(Color.parseColor("#CD5C5C"));
            //paint.setAlpha(0);
            canvas.drawCircle(x / 2, y / 2, radius, paint);
        }
    }

    static class _MyView1 extends View {

        int framesPerSecond = 60;
        long animationDuration = 10000; // 10 seconds

        Matrix matrix = new Matrix(); // transformation matrix

        Path path = new Path();       // your path
        Paint paint = new Paint();    // your paint

        long startTime;

        public _MyView1(Context context) {
            super(context);

            // start the animation:
            this.startTime = System.currentTimeMillis();
            this.postInvalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(RED);

            path.moveTo(10, 50);   // THIS TRANSFORMATIONS TO BE ANIMATED!!!!!!!!
            path.lineTo(40, 50);
            path.moveTo(40, 50);
            path.lineTo(50, 40);

            long elapsedTime = System.currentTimeMillis() - startTime;

            matrix.postRotate(30 * elapsedTime/1000);        // rotate 30° every second
            // matrix.postTranslate(100 * elapsedTime/1000, 0); // move 100 pixels to the right
            // other transformations...

//            canvas.concat(matrix);        // call this before drawing on the canvas!!

            canvas.drawPath(path, paint); // draw on canvas

            if(elapsedTime < animationDuration)
                this.postInvalidateDelayed( 1000 / framesPerSecond);
        }

    }


    public void animatePuls(int x, int y, int s) {
        //this.pulsView.setTranslationX(200);
        //this.pulsView.setTranslationY(300);
        if(this.pulsView == null){
            return;
        }
        this.pulsView.layout(x+s/2-100, y+s/2-100, x+s/2+100, y+s/2+100);
        //this.pulsView.offsetLeftAndRight(x+s/2);
        //this.pulsView.offsetTopAndBottom(y+s/2);
        // this.pulsView.setVisibility(View.VISIBLE);
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.puls);
        anim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                //         Activity_Main.this.pulsView.setVisibility(View.INVISIBLE);
            }
        });
        this.pulsView.startAnimation(anim);

    }

    public void imageViewAnimatedChange(final ImageView v, final int new_image) {
/*
        final Animation anim_out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
*/
        /*
        final ObjectAnimator a = new ObjectAnimator();
        a.setDuration(600);
        a.setProperty();
        */

        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.puls);

        final Animation anim_out = AnimationUtils.loadAnimation(this, R.anim.card_flip_left_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(this, R.anim.card_flip_left_in);

        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageResource(new_image);//.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }




////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class _PlaceholderFragment extends android.support.v4.app.Fragment {

        public _PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_activity_main, container, false);

            ImageView mImageView;
            //mImageView = (ImageView) rootView; //findViewById(R.id.imageViewId);
            mImageView = new ImageView(this.getActivity());

            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mImageView.setLayoutParams(lp);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setImageResource(R.drawable.image2);

            mImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Activity_Main.instance.flipCard();
                    //v.getId() will give you the image id

                }
            });

            //return rootView;
            return mImageView;
        }
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class _CardFrontFragment extends android.support.v4.app.Fragment {
        public _CardFrontFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_card_front, container, false);
        }
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class _CardBackFragment extends android.support.v4.app.Fragment {
        public _CardBackFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_card_back, container, false);
        }
    }

    private void _flipCard() {
        /*
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }
 
        // Flip to the back.
 
        mShowingBack = true;
 
        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.
 
        getFragmentManager()
                //getFragmentManager()
                .beginTransaction()
 
                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
 
                .setCustomAnimations(
                       R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
 
 
                        // Replace any fragments currently in the container view with a fragment
                        // representing the next page (indicated by the just-incremented currentPage
                        // variable).
                .replace(R.id.container, new CardBackFragment())
 
                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                .addToBackStack(null)
 
                        // Commit the transaction.
                .commit();
        */
        // Defer an invalidation of the options menu (on modern devices, the action bar). This
        // can't be done immediately because the transaction may not yet be committed. Commits
        // are asynchronous in that they are posted to the main thread's message loop.
        /*
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
        */
    }

}
 
 
