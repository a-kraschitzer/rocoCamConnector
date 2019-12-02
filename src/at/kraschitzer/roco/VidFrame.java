/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.kraschitzer.roco;

import FileOperations.FileOperation;
import at.kraschitzer.roco.util.HexCaster;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Norbert
 */
public class VidFrame extends javax.swing.JFrame {

    private static final byte[] IMAGE_START = HexCaster.unstringify("ffd8");
    private static final byte[] IMAGE_END = HexCaster.unstringify("ffd9");

    private static final int IMAGE_BUFFER_LENGTH = 400240;

    private final DataListener dataListener;

    private byte[] imageBuffer = new byte[IMAGE_BUFFER_LENGTH];

    private int imageBufferCnt = 0;
    private boolean inImage = false;
    private boolean imagePartStart = false;
    private boolean imagePartEnd = false;

    private int imgCount;

    /**
     * Creates new form VideoJFrame
     *
     * @param dataListener
     */
    public VidFrame(DataListener dataListener) {
        this.dataListener = dataListener;
        initComponents();
    }

    public void addData(byte[] data) {

        for (int offset = 0; offset < data.length; offset++) {
            if (inImage) {
                if (checkForImageEnd(data, offset)) {
                    imageBuffer[imageBufferCnt++] = IMAGE_END[0];
                    imageBuffer[imageBufferCnt++] = IMAGE_END[1];
                    imageBuffer = Arrays.copyOf(imageBuffer, imageBufferCnt);
                    if (imgCount % 1 == 0) {

//                          ###### PRINT DATA AS HEX
//                        for (int i = 0; i < imageBuffer.length; i++) {
//                            System.out.print(String.format("%02x", imageBuffer[i]) + " ");
//                        }
//                        System.out.println("");
//                          ###### WRITE IMAGE TO FILE
//                        try (FileOutputStream fos = new FileOutputStream("IMG/img"+imgCount+".jpg")) {
//                            fos.write(imageBuffer);
//                             }catch(Exception ex){
//                            ex.printStackTrace();
//                        }
                        Image im = Toolkit.getDefaultToolkit().createImage(imageBuffer);
                        videoPanel.setImage(im);

                        //System.out.println("Received image with length " + imageBuffer.length);
                    }
                    imgCount++;
                    //System.out.println(HexCaster.stringify(imageBuffer));
                    inImage = false;
                    continue;
                }
                imageBuffer[imageBufferCnt++] = data[offset];
            } else if (checkForImageStart(data, offset)) {
                imageBuffer = new byte[IMAGE_BUFFER_LENGTH];
                imageBuffer[0] = IMAGE_START[0];
                imageBuffer[1] = IMAGE_START[1];
                imageBufferCnt = 2;
                offset++;
                inImage = true;
            }
        }
    }

//    public static void main(String[] args) {
//        VidFrame f = new VidFrame(null);
//        f.setVisible(true);
//        f.setImage(HexCaster.unstringify("ffd8ffe000104a46494600010101006000600000ffe100224578696600004d4d002a00000008000101120003000000010001000000000000ffdb0043000201010201010202020202020202030503030303030604040305070607070706070708090b0908080a0807070a0d0a0a0b0c0c0c0c07090e0f0d0c0e0b0c0c0cffdb004301020202030303060303060c0807080c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0c0cffc00011080068005a03012200021101031101ffc4001f0000010501010101010100000000000000000102030405060708090a0bffc400b5100002010303020403050504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f02433627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1f2f3f4f5f6f7f8f9faffc4001f0100030101010101010101010000000000000102030405060708090a0bffc400b51100020102040403040705040400010277000102031104052131061241510761711322328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a35363738393a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000c03010002110311003f00fdfca28afca0ff0083937fe0b53f11bf60dd63e1dfc10fd9fe3f33e367c4cf2f503750e92754bed2ecdae85bda43676b24124171717b711cf0807cc68d6071e56f9e195003eaff00daf3fe0b8bfb2bfec21f1966f87bf14be2d69fe1df195a5a437975a65be8fa8eab25924c0b46266b3b7992191930e2390ac9e5c91bedd922337b07ecbffb6b7c23fdb53c2adac7c27f88de0ff1f59c3696b79771e8fa9c73dd6989748cf00bbb707ceb591c2483cb9d124063914a864603f087f651ff00836375ef3f4ff1e7ed45f0e7f690f8afe3bf135dff006feb5a2f84fc45e1386d60b81a8ddf9f6fa9ea37bacadd5f4977125bcef25b792d18ba65f39e5dc62f4ff117fc19dda3eade04f0e78ebe087c4af8c1f047e2368bf69d4adb49f8832699a95fdbea30480e9ecb7da24a12c7f7917986685aedd5658d822491b46c01fbbf457e00feccff00f05edfda3bfe08fbfb6459fece9fb745bffc24de12d3f65b45e378ed1ee35582c596386cf538a78c2ff6a69e7c895a46788df179672eef340d687f7fa800a28a2800a28a2800a28a2800afe747fe0e57b5b8fd8e3fe0bdbfb3bfed09e25f04e9fa6fc3d86ef41d50ea3a04f05c6a7e2a7d13528ae2f9e7859612b791c135ac0824778de18edb138c3c36ff00d1757e187fc1ebc9a4ea9f013e1ab6ada0e9f6bae687e20b61e16d69f5e4fb5ea96f796da97f6d5aa69c183f976cf63a03bdcbab0cdfc51a143e609003eeff0089bfb0e7ed59e29ff82b7dbfc5bf0e7ed39ff08bfc038f4ad36de5f027f64fdb7ce582e607bcd3becae05bafda9639dffb543fdb22fb4790a862456af40ff82bcffc136bfe1eb5fb1b5efc25ff008589e20f86ff0068d56cf55fb7e9d0fdaadaf3c8627ecd7b6be647f69b73bbcc11f989b6786de5c9f2b637c01fb1effc1c1da1fec6bf11bf654fd9c7e237857fe113f871ad7c15f0330f1feb9249a6dbd95e5c68664f3d0ed923bad3e493ec569e7936eb6d3c57c65678e3ca741f16744ff88513f61df8cff122cfc75f103e3e789be37fc4054f0dd9f8aae3161a56a33c1773a5ddf912f997170d1c32b5ddd47e5bde35bda4623b600cca01c07fc1db3f02ff00e1547fc117bf67cd37c47aa7fc279e37f0278af47f0ccbe32d4adb3aaeab8d0af96f27692479254fb54d6704d2a195f73c71966728ad5fa7dff049d39ff8258fecd3ff0064abc2ff00fa68b5afc71ff8380ff699f197edb3ff0006fd7c2ff8d1e30d1b50f0847f14be3058eb5a0784ef61064f0b6969a26a7690c42e7ca85af23ba7b6975149de2525353445dd1c71bb7e87ff00c1ae3ff2828f819ff71fff00d48353a00fbfe8af9bff00e0a61ff0553f847ff04a1f83563e30f8a5a96a1249ad5dfd8f45d03478a3b9d675c7053ce36f0c92469e5c28e1e4924748d0145dc649628e4f50fd957f690d07f6c0fd9c7c17f143c3167e20d3fc3fe3ad2a1d5ec2df5bd364d3efe28a55c81244ff00a3a178a45db24524913a48c01e8145145001451450015f99ff00f07457ec33e32fdb4ffe09eb7167e0993c5fe24f13685e20d3b5ed23c23a5e843508f507b2b5d545d6d9228c4b048f69772c9ba691e3964d3ed6da0885c5d8137e9851401fcd0fec07e1df81ff00f0712fec6df0f7f673f891e23ff855ff00b557c13d29747f0578cffb3e0b8ff84abc396cccc9a7f9086dc5c7d96d7e5fb3bb8993c9fb5472c8b25fc60fda43fe0d94fd9c7fe09ffac59de7c78fdbabc3fe1dd3ecfec9a95ff87a1f07a43e24d4f4e92e8405aced92fee6e4ef659104c9693a4651dd91962703ec0ff82f5ffc1b57f0b3e2df83be397ed39e16f177883c0fe36d3f4abff1c6afa447a7da5c685a9fd874b9659e28a08d20961b8ba9a05964b979a6cc92cee6373202bf0fff00c1bc3ff06f0fc2cff82b1fecb1e34f899f12fc69f10348fec9f153f8634dd37c332da5a797e4da5b5ccb3cd2dc4171e66ffb646aa8a91ecf25c9693cc02300cfff0082f97fc17cbe0bff00c149bf62ff00873f05fe0bfc39f18783f44f07f882df582758b2b1d2ed74cb7b4b1b8b3b5b2b4b5b49a643194ba7fe288442de355570e4c7fb5df04be22fc39ff820bffc1153e18c9f15a3ff008416dbc0be15b28754d161bb5d42ff0051f11dcc4d757b6567fbd659ee26bd7bb70a927928bbdf7476f133a687ec5fff000401fd977f6219fc21aa685f0eb4ff001578cbc136ad6ba7f8a7c510c37da996fed093508ae5d5238edbed90caea915dac0b70914514624d8b8afe74bfe0b05e1efda6bf6ceff82abfc57f873a8788bc41fb4a7883e1eeab772d9d8fc3fd3ef753d2340b3616c9225a69d099bec3e57fa2db5ca65d96e61292cd3ca0cae01f4fff00c13a7f679f88ff00f0581fdb26dff6d2fdac74bff84c3e1caeaafa4f83bc166c5a4ff8587a9c6b706d341d16ca499231a7da48b2cf34b7121b63f66b96ba778c6a57307edffed23ff0592fd9c7f632d1ecff00e16f7c4ef0ff00c3ff001349f648f50f08cd729ac788f419ee2d45d2c37965a635d491e2323328dd012c9b65612465ff00043f666ff83647fe0a11e26d3b59b39b5ed3fe0cda5af87dfc26906b5f1024f2f56d1af279ee2eb4c8d74a3763ec66767965b79bcb8e47b9dc16463215fd00fd90ff00e0cc1f80bf087518752f8b9e38f187c62bcb6bb99d74fb78ff00e11ad1ae6dde00891cd14324b76648e42f28922bc883111a942aafe60073ff001aff00e0f70f82fa17856de6f873f06fe2878ab5b6bb549ed3c497563e1fb58edf63969167824bd7690388c08cc4a08663bc150adedfff000403ff0082d1fc7eff0082a16b3a859fc56f80ff00f08bf8665d2af35bd17e22689a76a363e1bd53c8bab5b4fece8c5c89a396e048f76e644bbe96ec9e4828ef5f5ffc27ff0082527eccbf03bfe11993c2df003e0fe97a8783fecada46a9ff00089d94faada4b6db0c171f6d9236b97b856457f3de4694b8de5cb65abe80a0028a28a002bf2c3fe0e9bfdaf3f696fd89fe017c2bf1e7c059b50d07c3ba1f882eae7c5fe25b1b58ef1b4b91ed85a58417304a5e17b39cdddd6e3341222dc416443c52f9424fd4facff16784f4bf1ef85752d0b5dd334fd6b44d6ad25b0d434fbfb74b9b5bfb7950a4b0cb13829246e8ccac8c0860482083401f921ff0547ff82b27857f6eaff8360bc75f173c1dae7fc209abf8effb3fc2d71a27f6fc2351b4d40eab6c9a8691ba275697ccb14ba97cb2a8f358c9e63c488eca3d83fe0d3ff85ba0fc3fff0082257c39d5b48b1fb1ea1e38d575bd6f5b97ce924fb6de26a77160b2e19884c5ad8dac7b502afeeb763733337e20ff00c1c21ff0486f157fc129be3b6b1ff085d97882dff665f893aadb6a3e1fdbaa4d7b6767a8c36f362c2f32062e20f3efbecad3798ed6b2be269645badbefedff00054af8c9fb627ecb1f017f62ff00d817c37e20d2756f0bfc3fd26fbc71e21f0d32f87ef2e35186d219f508e0b966856cadc5f3bfda2f1de37bcba97cb5629266f003fa5eafe6c7f6f3f12f8cff00e0dd0ff8389ef7e356931ea1a97c2ef8e5753ebbaab4b602fa4d4f4bd42f639f5bd3e2764b7896f2deed3cf85239b291b69fe748eb2ca8ff007fff00c10a3fe0dfaf1f7fc131ee2f2fbe277c5ed3fc75a2eb56af35c7c37b0b3b8b9f0be9bacc7a8d95cd9eb31497122acb791269f01594d945246ec02c8444acff007ffed79fb17fc2ff00dbd3e0d4df0ffe2e783f4ff1a78526bb86fd6d2e2496de4b6b8889293433c2e93432005d0bc4ea5a3924424a48eac01e01fb6f7fc176be02fec75fb06687f1dacfc4da7f8fb4df1f5a3bf80b48d26e7cbbaf16dc2f0f180cbbada381c85ba9254cdb10636433b47049e5ff00f0482ff82fd7857f6cff00d902fbe247c78f13fc1ff827a81f15cfe19d2ad350f1243a5c3aafd8f4ad267bb9e2379302f9babe9182267c98a5b78d9a465334bf863ff0731fc4ef06cfff0005156f84bf0cf4fd3f48f87bf017c3fa67832d2df4dd60ea16b777b6f656f14f71213922f22822b2d326691e5988d1615924fddac71fa7ff00c104bfe0de1f0aff00c149bc3b71e2cf8cde34f107843c3faf69536a1e0cd1bc3d2c306abe2282def9acaef5013dc41341f67b6b88cdbc90a2b4eaf7104928b78e5b537801fd377c14fda17c03fb4a7856e35df873e38f07f8ff0044b4bb6b09f50f0deb36daadac370a88ed0b4b03ba2c81248d8a139024538c30cf615f8e3fb507ec55ad7fc12d3f62e5fd927f619f8cda85a7c78f899e20baf1d9f0f788fc43a541e28d7b446b15d3afe3d3ae1ade086da45315bdc46dbe0b829637ef04ac6ddd17f47bfe09bda3fc6dd0bf625f00c1fb466ada7eb5f1a1ed26b8f12dc5925b2c71bcb7334b04045ac71c1e6436ef042e61531992272af20224600f70a28a2800a28a2803e7ff00f82aa7c0cf027ed1ff00f04e2f8d1e13f899aa7f607826e3c2979a86a5acfd9ae2ebfb07ec69f6d8b50f22dd9259fecd35bc771e42b7ef7c9f2c860e54fe28ff00c191ff00b2feabaa7c7df8c9f1a266d42d344d0fc3f0f82ad03e9cff0065d52e2f2e62bc9fcbba2c13ccb64b1b7df105638bf898941b449f607fc1db3fb7c7807c0fff0004d1f187c1ad3fc49e0fd7be2178cbc41a368fa9f87edfc456dfdb3e1bb7474d5d6f66b25dd3796c2ce0886e118c5ec6fb8fcaafe9ff00f06a65ef813fe1cdbe0ad3bc1ba9787f56d5b49d57518fc6175a569b7169ff001389a517821b879e085ae2e21b1b9b081e551247fb9091cb2246a6803f47e8a28a00fcb1b4ff0083547e11f887fe0a5bf113e3af8e7c59a878f7c1be3fbbd6b5493c03a8e931c71c57baac722dc3c97cb26f68e37b9b9920f2a286686416ae272f01697e0fff0082187c09fdb13fe09e9a37ed13a7f8afc23e20f84bf0efe1ae9577e38b8d7bc5f77a9d9689a7eb9a5db5daa7936d6f1cf16b5a7ddc2b2c37a2da2909b78adae20b98aeadec3ccfe8fa8a00fe4475dff828ff008cbe36ff00c159fe2efedb9e13bbd3fe1fe97e17ba1159ea57ba28bbbab2b2beb75f0ed8c76d6725c7d9aeb5c4d35e6bf5b592e52195b4cbd9326185a3aec7e167fc1cdfe2af841ac7c4eb4f0ffc19f00785fc31f12b55b2b9b89bc37713697e2f7b182ead4cb6b79e200af757f717166ba923ea330fb72ddead35dc73c6523847d9ff001fbfe0cfcf197ed07ff052ef10f8baebc71f0bfc2bf007c41e2037cba7f857471a36b3a5e97b014d3edb4f86d45847226d5b7fb41958b806ea48e4919e16e43fe0bf3ff0427ff8473e3b7c2d6f025bfc1ff85bf0162d2bfe11dd12d340f0c797e2ad4f5786dcceda6b15dd3ebbac5f5bdaca6c7ed3730c734d01b66786e2759af803f43bfe0dfbff0082b7f8cbfe0abbe16f8bdaa7883c2fa858f877c27e2081b40d767b216cb3a5fa4b752e8a4c60c371269676402e90a49716f2d9cb2dbc12bb799fa1f5f2ff00fc11ff00fe09b1a17fc12b7f61ef0dfc33d35bed3e20b9dbadf8befd6f64ba8752d7268218eee580ba47b6dd7c98e28944687ca8632e0cad23b7d41401f3ff00fc142bfe0a75f06ffe097ff0b2d7c53f173c51fd93fdadf688f44d22ceddaef55f104f0c265686da05ff00802196531c11bcd089258fcc527f147c73ff000575fdb7bfe0e06f8fbe24f86dfb23e9da87c2bf85ba45dc26ef58b3ba5d2f53b2b096e6dd60bad53550ecf6f26f865985b69c44cd0b5cc58bd5899cfe907edf7ff06e5fc2ff00f8292ffc142f4bf8d9f12fc5de30b8f0ed8f87ecb4ab8f07595d4a91ea1716d74f28637524b21b7b39207689ed6ce2b725d9e7599659242ff777c2df84fe15f81de04b1f0b7827c33e1ff07f8674bf33ec7a4689a743a7d85a799234b2797042ab1a6e91ddced032cec4f249a00fc51fd97bfe0c8ff00e99e1569be347c63f186b9adddda5ab8b4f055adb6956ba5dc6c6375199eee3b96bb8f79411c9e55b1c46c5932e163f982ead7f6f0ff835fb50f1ae93e0cbcf0878dbe03f86fc411eb57cd25969f7d65a98d5206b3b2d42fed62946afa74723590854b4b1dbfdaacda249670d99bfa6dae3f5bf803e0df13f8abc59ac6abe1ed3f56bbf1d787ed7c2be208efd4dd5aeada5db3df3c5692dbc85a168c1d4af770d99904e55cb2aa8500f3fff00826f7edbda5ffc147bf625f00fc68d1f43d43c3567e35b499e4d2ef2649a4b1b8b7b99ad2e23122e049189e097648550bc7b1992362517dc2b9ff85bf09fc2bf03bc0963e16f04f867c3fe0ff0ce97e67d8f48d134e874fb0b4f3246964f2e0855634dd23bb9da0659d89e4935d050014515e7ff00b58ffc277ff0cb1f12ff00e156ff00c94dff00845754ff008447fe3dff00e431f6497ec5ff001f1fb8ff008f8f2bfd77eeff00bff2e6803d02bf9e1ff82b1ffc1c89fb4378bbfe0a3bae7c15fd9af5ef0ffc25d27e1deaba9f862f751f12cda0db7fc245a859bbadd4f35dead9b3b4b746b7912dd3cc47933b9d99e68e087cff00fe0933ff000744f8abfe09d3a3eb9f0aff00698f0c7c60f891a847e2bbfb9d5bc43abf8966bef1278736dac36e34c161a80527cbbab67deaf750f966e253b0b2957e83fe0b93fb5aff00c138ff006f7f859e19fda0b4e93e206bdf1935af2f46baf0c7852f20f0ceab79e5c36d23beb6f756577127d92190411dcc092fda1ff72924f15b17b500f943f68bff0082857eda5fb287c33f8277baa7ed8da7f8a6f2ceef53bcb1d17c23f132cfc53a9e8af0ddc32635f9acda586fa39cb2b411dd5c5dc7e4acb16d8d449157f57bfb3d7c6bd2ff00694f805e07f88da15bea169a278fbc3f61e24d3e0bf8d23ba86def2da3b8892554674590248a182bb0041c311c9fe687fe084bff0006caf8cbf6dcf157867e297c74d1750f09fc079ad21d6b4fb27b9106a7e3c4676f2a28d51bceb5b37082479dc4724b0c917d9f226fb4c3fd377c27f85ba0fc0ef859e19f04f85ac7fb2fc33e0fd2ad744d22cfce927fb259db4290c1179923348fb63455dceccc719249c9a00e828a28a0028a28a0028a28a0028a28a00e3fe35fecf7e01fda53c2b6fa17c46f03f83fc7fa25a5dadfc1a7f89346b6d56d61b85474599629d1d16409248a1c0c81230ce18e7e00f829ff000699fec7ff0006be3edc78e65d07c61e32b35bb6bdd3fc2be24d656ef40d29fed293c416248a39ae238c279423bc9ae239237612acac77028a00fd2fa28a2803ffd9"));
//    }
//    public void setImage(byte[] b) {
//        videoPanel.setImage(Toolkit.getDefaultToolkit().createImage(b));
//    }
    private boolean checkForImageStart(byte[] receiveBuffer, int offset) {
        if (receiveBuffer[offset] == IMAGE_START[0] && offset < receiveBuffer.length - 1 && receiveBuffer[offset + 1] == IMAGE_START[1]) {
            return true;
        }
        if (imagePartStart && receiveBuffer[0] == IMAGE_START[1]) {
            return true;
        }
        if (receiveBuffer[offset] == IMAGE_START[0] && offset == receiveBuffer.length - 1) {
            imagePartStart = true;
        }
        return false;
    }

    private boolean checkForImageEnd(byte[] receiveBuffer, int offset) {
        if (receiveBuffer[offset] == IMAGE_END[0] && offset < receiveBuffer.length - 1 && receiveBuffer[offset + 1] == IMAGE_END[1]) {
            return true;
        }
        if (imagePartEnd && receiveBuffer[0] == IMAGE_END[1]) {
            return true;
        }
        if (receiveBuffer[offset] == IMAGE_END[0] && offset == receiveBuffer.length - 1) {
            imagePartEnd = true;
        }
        return false;
    }

    public void setLocationCoordinates(int x, int y) {
        this.setLocation(x, y);
    }

    public void setName(String name) {
        this.setTitle(name);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exitButton = new javax.swing.JButton();
        videoPanel = new at.stejskal.global.ImagePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        exitButton.setText("Disconnect");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout videoPanelLayout = new javax.swing.GroupLayout(videoPanel);
        videoPanel.setLayout(videoPanelLayout);
        videoPanelLayout.setHorizontalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        videoPanelLayout.setVerticalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 393, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 559, Short.MAX_VALUE)
                        .addComponent(exitButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exitButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        //dataListener.removeSource(this);
    }//GEN-LAST:event_exitButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exitButton;
    private at.stejskal.global.ImagePanel videoPanel;
    // End of variables declaration//GEN-END:variables

}
