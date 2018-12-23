package com.itrus.ikey.safecenter.TOPMFA.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils
{
  public static byte[] md5Sha1Hash(String hashAlgorithm, byte[] origin)
  {
    byte[] result = (byte[])null;
    if ((origin == null) || (origin.length <= 0)) {
      result = (byte[])null;
    }
    MessageDigest digester = null;
    try
    {
      if ("MD5".equalsIgnoreCase(hashAlgorithm))
      {
        digester = MessageDigest.getInstance("MD5");
        digester.update(origin);
        result = digester.digest();
      }
      else if (("SHA-1".equalsIgnoreCase(hashAlgorithm)) || ("SHA1".equalsIgnoreCase(hashAlgorithm)))
      {
        digester = MessageDigest.getInstance("SHA-1");
        digester.update(origin);
        result = digester.digest();
      }
    }
    catch (Exception ex)
    {
      result = (byte[])null;
    }
    return result;
  }
  
  public static final String MD5Encrpytion(String source)
  {
    char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    try
    {
      byte[] strTemp = source.getBytes();
      
      MessageDigest mdTemp = MessageDigest.getInstance("MD5");
      mdTemp.update(strTemp);
      byte[] md = mdTemp.digest();
      int j = md.length;
      char[] str = new char[j * 2];
      int k = 0;
      for (int i = 0; i < j; i++)
      {
        byte byte0 = md[i];
        str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
        str[(k++)] = hexDigits[(byte0 & 0xF)];
      }
      for (int m = 0; m < str.length; m++) {
        if ((str[m] >= 'a') && (str[m] <= 'z')) {
          str[m] = ((char)(str[m] - ' '));
        }
      }
      System.out.println("[MD5Utils] [source String]" + source);
      System.out.println("[MD5Utils] [MD5    String]" + new String(str));
      return new String(str);
    }
    catch (Exception e) {}
    return null;
  }
  
  public static String SHA1Encrpytion(String str)
  {
    String result = "";
    if ((str == null) || (str.equals(""))) {
      return "";
    }
    try
    {
      MessageDigest alga = MessageDigest.getInstance("SHA-1");
      alga.update(str.getBytes());
      byte[] r = alga.digest();
      if (r != null) {
        result = new String(r);
      }
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    return result;
  }

}
