package com.example.adriana.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER;
import static com.example.adriana.inventoryapp.ProductContract.ProductEntry._ID;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri currentUri;

    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneNrEditText;

    private TextView productQuantityText;
    private Button subtractProductButton;
    private Button addProductButton;

    private Button orderProductButton;

    private boolean didProductChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            didProductChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Intent intent = getIntent();
        currentUri = intent.getData();

        if (currentUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        productNameEditText = findViewById(R.id.product_name_edit_text);
        productPriceEditText = findViewById(R.id.product_price_edit_text);
        supplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        supplierPhoneNrEditText = findViewById(R.id.supplier_phone_number_edit_text);

        productQuantityText = findViewById(R.id.product_quantity_text_view);

        subtractProductButton = findViewById(R.id.subtract_product_button);
        subtractProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(productQuantityText.getText().toString().trim());
                decrement(quantity);
            }
        });

        addProductButton = findViewById(R.id.add_product_button);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(productQuantityText.getText().toString().trim());
                increment(quantity);
            }
        });

        orderProductButton = findViewById(R.id.order_product_button);
        orderProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditProductActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, 0);
                } else {
                    String phoneNr = supplierPhoneNrEditText.getText().toString();

                    if (phoneNr.isEmpty() || phoneNr == null) {
                        Toast.makeText(getApplicationContext(), R.string.empty_phone_number, Toast.LENGTH_SHORT).show();
                    } else {

                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNr));
                        startActivity(intent);
                    }
                }
            }
        });

        productNameEditText.setOnTouchListener(touchListener);
        productPriceEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneNrEditText.setOnTouchListener(touchListener);
    }

    private void increment(int quantity) {
        if (quantity == 100)
            Toast.makeText(EditProductActivity.this, R.string.maximum_nr_of_products, Toast.LENGTH_SHORT).show();
        quantity = quantity + 1;
        productQuantityText.setText(String.valueOf(quantity));
    }

    private void decrement(int quantity) {
        if (quantity == 0)
            Toast.makeText(EditProductActivity.this, R.string.minimum_nr_of_products, Toast.LENGTH_SHORT).show();
        else {
            quantity = quantity - 1;
            productQuantityText.setText(String.valueOf(quantity));
        }
    }

    private boolean isProductValid(String productName, double price, int quantity, String supplier, String supplierPhone) {
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NavUtils.navigateUpFromSameTask(EditProductActivity.this);
            }
        };

        if (productName == null || productName.isEmpty()
                || price == 0.0 || price < 0.0
                || quantity < 0 || quantity == -1
                || supplier == null || supplier.isEmpty()
                || supplierPhone == null || supplierPhone.isEmpty()) {
            showInvalidProductDialog(discardButtonClickListener);
            return false;
        }

        return true;
    }

    private void saveProduct() {
        String productName = null;
        Double productPrice = 0.0;
        Integer productQuantity = -1;
        String supplierName = null;
        String supplierPhoneNr = null;

        String productNameText = productNameEditText.getText().toString().trim();
        if (productNameText != null && !productNameText.isEmpty())
            productName = productNameText;

        String productPriceText = productPriceEditText.getText().toString().trim();
        if (productPriceText != null && !productPriceText.isEmpty())
            productPrice = Double.valueOf(productPriceText);

        String productQuantityText = this.productQuantityText.getText().toString().trim();
        if (productQuantityText != null && !productQuantityText.isEmpty())
            productQuantity = Integer.valueOf(productQuantityText);

        String supplierNameText = supplierNameEditText.getText().toString().trim();
        if (supplierNameText != null && !supplierNameText.isEmpty())
            supplierName = supplierNameText;

        String supplierPhoneNrText = supplierPhoneNrEditText.getText().toString().trim();
        if (supplierPhoneNrText != null && !supplierPhoneNrText.isEmpty())
            supplierPhoneNr = supplierPhoneNrText;

        // if nothing has been changed, the user can go back to main activity
        if (currentUri == null && productName == null && productPrice == null
                && productQuantity == null && supplierName == null && supplierPhoneNr == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, productName);
        values.put(COLUMN_PRODUCT_PRICE, productPrice);
        values.put(COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        values.put(COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNr);

        if (isProductValid(productName, productPrice, productQuantity, supplierName, supplierPhoneNr)) {
            if (currentUri == null) {
                Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

                if (newUri != null) {
                    Toast.makeText(getApplicationContext(), R.string.toast_msg_product_added_successfully, Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                int rowsAffected = getContentResolver().update(currentUri, values, null, null);

                if (rowsAffected != 0) {
                    Toast.makeText(this, R.string.toast_msg_product_updated_successfully, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!didProductChanged) {
                    NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!didProductChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID,
                COLUMN_PRODUCT_NAME,
                COLUMN_PRODUCT_PRICE,
                COLUMN_PRODUCT_QUANTITY,
                COLUMN_PRODUCT_SUPPLIER_NAME,
                COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this,
                currentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1)
            return;

        if (data.moveToFirst()) {
            int productColumnIndex = data.getColumnIndex(COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = data.getColumnIndex(COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = data.getColumnIndex(COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneNrColumnIndex = data.getColumnIndex(COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String productName = data.getString(productColumnIndex);
            Double productPrice = data.getDouble(productPriceColumnIndex);
            int productQuantity = data.getInt(productQuantityColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            int supplierPhoneNr = data.getInt(supplierPhoneNrColumnIndex);

            productNameEditText.setText(productName);
            productPriceEditText.setText(Double.toString(productPrice));
            productQuantityText.setText(Integer.toString(productQuantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneNrEditText.setText(Integer.toString(supplierPhoneNr));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productNameEditText.setText("");
        productPriceEditText.setText("");
        productQuantityText.setText(Integer.toString(1));
        supplierNameEditText.setText("");
        supplierPhoneNrEditText.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard_option, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing_option, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showInvalidProductDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.product_not_valid_dialog_msg);
        builder.setPositiveButton(R.string.discard_option, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing_option, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentUri != null) {
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.toast_msg_failed_to_delete, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.toast_msg_deleted_successfully, Toast.LENGTH_SHORT).show();
            }
        }

        // close this activity
        finish();
    }
}
