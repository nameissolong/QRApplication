package recycler.qtc.edu.com.qrapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rock.qrcodelibrary.CaptureActivity;
import com.zxing.encoding.EncodingHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.zxing.encoding.EncodingHandler.creatBarcode;
import static com.zxing.encoding.EncodingHandler.createQRCode;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intView();
        Toast.makeText(this, "提交了", Toast.LENGTH_SHORT).show();
    }

    private void intView() {
        imageView = ((ImageView) findViewById(R.id.imageView));
        editText = ((EditText) findViewById(R.id.et));
    }

    public void btnClick(View view) {
        String info = editText.getText().toString().trim();
        Bitmap bitmap;
        switch (view.getId()) {
            case R.id.qr:
                if (info != null) {
                    bitmap = EncodingHandler.createQRCode(info, 200);//获取二维码图片
                    imageView.setImageBitmap(bitmap);
                }
                break;
            case R.id.bar:
                bitmap = EncodingHandler.creatBarcode(this, info, 200, 50, true);
                imageView.setImageBitmap(bitmap);
                break;
            case R.id.qrWithLogo:
                bitmap=EncodingHandler.createQrCodeLogo(info, 200, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
                imageView.setImageBitmap(bitmap);
                break;
            case R.id.scanningQr:
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, 200);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==200&&resultCode==RESULT_OK&&data!=null)
        {
            String stringExtra = data.getStringExtra(CaptureActivity.RESULT);
            Toast.makeText(this, stringExtra, Toast.LENGTH_SHORT).show();
        }
    }
}
