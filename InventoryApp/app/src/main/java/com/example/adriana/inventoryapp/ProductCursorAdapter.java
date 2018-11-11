package com.example.adriana.inventoryapp;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Adriana on 8/4/2018.
 */

public class ProductCursorAdapter extends CursorAdapter
{
    public ProductCursorAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor)
    {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        int productIdColIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        int productNameColIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int productPriceColIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        int id = cursor.getInt(productIdColIndex);
        String productName = cursor.getString(productNameColIndex);
        double productPrice = cursor.getDouble(productPriceColIndex);
        int productQuantity = cursor.getInt(productQuantityColIndex);

        nameTextView.setText(productName);
        priceTextView.setText("Price: " + productPrice +"$");
        quantityTextView.setText("Quantity: " + productQuantity);

        Button saleButton = view.findViewById(R.id.sale);
        saleButton.setTag(R.id.TAG_ID_1, quantityTextView);
        saleButton.setTag(R.id.TAG_ID_2, productQuantity);

        final Uri uri = Uri.parse(ProductContract.ProductEntry.CONTENT_URI + "/" + id);

        saleButton.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v)
            {
                TextView quantityView = ( TextView ) v.getTag(R.id.TAG_ID_1);

                int newProductQuantity = ( int ) v.getTag(R.id.TAG_ID_2);

                if (newProductQuantity <= 0)
                    Toast.makeText(context, R.string.no_product_to_sale, Toast.LENGTH_SHORT).show();
                else
                {
                    newProductQuantity--;
                    quantityView.setText("Quantity: " + newProductQuantity);

                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newProductQuantity);

                    int updatedRows = context.getContentResolver().update(uri, values, null, null);

                    if (updatedRows > 0)
                    {
                        context.getContentResolver().notifyChange(uri, null);
                        Toast.makeText(context, R.string.sale_recorded, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
