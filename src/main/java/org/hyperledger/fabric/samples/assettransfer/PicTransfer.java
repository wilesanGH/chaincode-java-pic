/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

@Contract(
        name = "pic",
        info = @Info(
                title = "pic Transfer",
                description = "The hyperlegendary pic transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public final class PicTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    /**
     * Creates some initial Pics on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitPicLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreatePic(ctx, "Pic1", "e1", "insdfd", "20", "2020-12-05 12:00:00");
        CreatePic(ctx, "Pic2", "e2", "inasdd", "30", "2020-12-05 12:00:00");
        CreatePic(ctx, "Pic3", "e3", "in1222", "40", "2020-12-05 12:00:00");
        CreatePic(ctx, "Pic4", "e4", "out222", "60", "2020-11-05 12:00:00");
        CreatePic(ctx, "Pic5", "e5", "in2222", "70", "2021-12-05 12:00:00");
        CreatePic(ctx, "Pic6", "e6", "out222", "30", "2020-12-05 12:00:00");

    }

    /**
     * Creates a new Pic on the ledger.
     *
     * @param ctx the transaction context
     * @return the created Pic
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Pic CreatePic(
            final Context ctx,
            final String picId,
            final String eventId,
            final String hashContext,
            final String picPath,
            final String createDate
    ) {
        ChaincodeStub stub = ctx.getStub();

        if (PicExists(ctx, picId)) {
            String errorMessage = String.format("Pic %s already exists", picId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, PicTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        Pic pic = new Pic(picId, eventId, hashContext, picPath, createDate);
        String picJSON = genson.serialize(pic);
        stub.putStringState(picId, picJSON);

        return pic;
    }

    /**
     * Retrieves an Pic with the specified ID from the ledger.
     *
     * @param ctx   the transaction context
     * @param picId the ID of the Pic
     * @return the Pic found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Pic ReadPic(final Context ctx, final String picId) {
        ChaincodeStub stub = ctx.getStub();
        String picJSON = stub.getStringState(picId);

        if (picJSON == null || picJSON.isEmpty()) {
            String errorMessage = String.format("PicPic %s does not exist", picId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, PicTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Pic pic = genson.deserialize(picJSON, Pic.class);
        return pic;
    }

    /**
     * Updates the properties of an Pic on the ledger.
     *
     * @param ctx the transaction context
     * @return the transferred Pic
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Pic UpdatePic(
            final Context ctx,
            final String picId,
            final String eventId,
            final String hashContext,
            final String picPath,
            final String createDate
    ) {
        ChaincodeStub stub = ctx.getStub();

        if (!PicExists(ctx, picId)) {
            String errorMessage = String.format("Pic %s does not exist", picId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, PicTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Pic newPic = new Pic(
                picId,
                eventId,
                hashContext,
                picPath,
                createDate
        );
        String newPicJSON = genson.serialize(newPic);
        stub.putStringState(picId, newPicJSON);

        return newPic;
    }

    /**
     * Deletes Pic on the ledger.
     *
     * @param ctx   the transaction context
     * @param picId the ID of the Pic being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeletePic(final Context ctx, final String picId) {
        ChaincodeStub stub = ctx.getStub();

        if (!PicExists(ctx, picId)) {
            String errorMessage = String.format("Pic %s does not exist", picId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, PicTransferErrors.ASSET_NOT_FOUND.toString());
        }

        stub.delState(picId);
    }

    /**
     * Checks the existence of the Pic on the ledger
     *
     * @param ctx   the transaction context
     * @param pidId the ID of the Pic
     * @return boolean indicating the existence of the Pic
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean PicExists(final Context ctx, final String pidId) {
        ChaincodeStub stub = ctx.getStub();
        String picJSON = stub.getStringState(pidId);

        return (picJSON != null && !picJSON.isEmpty());
    }

    /**
     * Changes the owner of a Pic on the ledger.
     *
     * @param ctx        the transaction context
     * @param picId      the ID of the Pic being transferred
     * @param newPicPath the new number
     * @return the updated Pic
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Pic TransferPic(final Context ctx, final String picId, final String newHashContext, final String newPicPath) {
        ChaincodeStub stub = ctx.getStub();
        String picJSON = stub.getStringState(picId);

        if (picJSON == null || picJSON.isEmpty()) {
            String errorMessage = String.format("Pic %s does not exist", picId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, PicTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Pic pic = genson.deserialize(picJSON, Pic.class);

        Pic newPic = new Pic(
                pic.getPicId(),
                pic.getEventId(),
                newHashContext,
                newPicPath,
                pic.getCreateDate()
        );
        String newPicJSON = genson.serialize(newPic);
        stub.putStringState(picId, newPicJSON);

        return newPic;
    }

    /**
     * Retrieves all Pics from the ledger.
     *
     * @param ctx the transaction context
     * @return array of Pics found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllPics(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Pic> queryResults = new ArrayList<Pic>();

        // To retrieve all Pics from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'Pic0', endKey = 'Pic9' ,
        // then getStateByRange will retrieve Pic with keys between Pic0 (inclusive) and Pic9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            Pic pic = genson.deserialize(result.getStringValue(), Pic.class);
            queryResults.add(pic);
            System.out.println(pic.toString());
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    private enum PicTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }
}
