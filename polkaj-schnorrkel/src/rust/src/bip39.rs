use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring, jbyteArray, jboolean, jint};

use bip39_rs::{Mnemonic, MnemonicType, Language, Seed};
use hmac::Hmac;
use pbkdf2::pbkdf2;
use sha2::Sha512;

#[no_mangle]
pub extern "system" fn Java_io_emeraldpay_polkaj_schnorrkel_BIP39_generate(env: JNIEnv, _class: JClass, words: jint) -> jstring {
    let phrase = match MnemonicType::for_word_count(words as usize) {
        Ok(p) => Mnemonic::new(p, Language::English).into_phrase(),
        Err(_) => return env.new_string("Invalid word count").unwrap().into_inner(),
    };
    env.new_string(phrase).unwrap().into_inner()
}

#[no_mangle]
pub extern "system" fn Java_io_emeraldpay_polkaj_schnorrkel_BIP39_toEntropy(env: JNIEnv, _class: JClass, phrase: JString) -> jbyteArray {
    let phrase: String = env.get_string(phrase).unwrap().into();
    let entropy = match Mnemonic::from_phrase(&phrase, Language::English) {
        Ok(m) => m.entropy().to_vec(),
        Err(_) => return env.byte_array_from_slice(&[]).unwrap(),
    };
    env.byte_array_from_slice(&entropy).unwrap()
}

#[no_mangle]
pub extern "system" fn Java_io_emeraldpay_polkaj_schnorrkel_BIP39_toMiniSecret(env: JNIEnv, _class: JClass, phrase: JString, password: JString) -> jbyteArray {
    let phrase: String = env.get_string(phrase).unwrap().into();
    let password: String = env.get_string(password).unwrap().into();
    let mini_secret = match Mnemonic::from_phrase(&phrase, Language::English) {
        Ok(m) => {
            let mut res = [0u8; 64];
            let mut seed = vec![];
            seed.extend_from_slice(b"mnemonic");
            seed.extend_from_slice(password.as_bytes());
            pbkdf2::<Hmac<Sha512>>(m.entropy(), &seed, 2048, &mut res);
            res[..32].to_vec()
        },
        Err(_) => return env.byte_array_from_slice(&[]).unwrap(),
    };
    env.byte_array_from_slice(&mini_secret).unwrap()
}

#[no_mangle]
pub extern "system" fn Java_io_emeraldpay_polkaj_schnorrkel_BIP39_toSeed(env: JNIEnv, _class: JClass, phrase: JString, password: JString) -> jbyteArray {
    let phrase: String = env.get_string(phrase).unwrap().into();
    let password: String = env.get_string(password).unwrap().into();
    let seed = match Mnemonic::from_phrase(&phrase, Language::English) {
        Ok(m) => Seed::new(&m, &password).as_bytes()[..32].to_vec(),
        Err(_) => return env.byte_array_from_slice(&[]).unwrap(),
    };
    env.byte_array_from_slice(&seed).unwrap()
}

#[no_mangle]
pub extern "system" fn Java_io_emeraldpay_polkaj_schnorrkel_BIP39_validate(env: JNIEnv, _class: JClass, phrase: JString) -> jboolean {
    let phrase: String = env.get_string(phrase).unwrap().into();
    Mnemonic::validate(&phrase, Language::English).is_ok() as jboolean
}