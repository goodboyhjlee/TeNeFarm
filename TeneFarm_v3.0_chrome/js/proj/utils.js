function pad(n, width) {
    n = n + '';
    return n.length >= width ? n : new Array(width - n.length + 1).join('0') + n;
  }



var gutil =  {
  boolean : function(data) {
    return data == "true";
  },
  decodeFloat : function(bytes, signBits, exponentBits, fractionBits, eMin, eMax, littleEndian) {
    var totalBits = (signBits + exponentBits + fractionBits);

    var binary = "";
    for (var i = 0, l = bytes.length; i < l; i++) {
      if (bytes[i]) {
        var bits = bytes[i].toString(2);

        while (bits.length < 8) 
          bits = "0" + bits;
    
        if (littleEndian)
          binary = bits + binary;
        else
          binary += bits;  
      }
      
    }
  
    var sign = (binary.charAt(0) == '1')?-1:1;
    var exponent = parseInt(binary.substr(signBits, exponentBits), 2) - eMax;
    var significandBase = binary.substr(signBits + exponentBits, fractionBits);
    var significandBin = '1'+significandBase;
    var i = 0;
    var val = 1;
    var significand = 0;
  
    if (exponent == -eMax) {
        if (significandBase.indexOf('1') == -1)
            return 0;
        else {
            exponent = eMin;
            significandBin = '0'+significandBase;
        }
    }
  
    while (i < significandBin.length) {
        significand += val * parseInt(significandBin.charAt(i));
        val = val / 2;
        i++;
    }
  
    return sign * significand * Math.pow(2, exponent);
  },
  
  floatToBytes: function(number) {
    var buffer = new ArrayBuffer(4);
    var floatView = new Float32Array(buffer);
    floatView[0] = number;
    var barr = new Int8Array(floatView.buffer); 
    return barr;
  },
  
  byte2Int : function(x) {
    var val = 0

    val = (  (x[0] << 8) | (x[1] & 0x00FF) ) ;
    let isMinus = ((x[0] >> 7) == 1) ? -1 : 1;
    if (isMinus < 0) {
      
      let pad16 = pad(val.toString(2),16);
      let compeVal = "";
      for (var kk=0; kk<16; kk++) {
        compeVal += (pad16[kk] == "0" ? "1" : "0");
      }
      val = Number(parseInt(compeVal, 2))+1;
    } 

    val = val*isMinus;
    return val;
    
  },
  
  int2Bytes : function(x) {
    var bytes = [];
    var i = 4;
    do {
    bytes[--i] = x & (255);
    x = x>>8;
    } while ( i )
      return bytes;
  },
  
  bin2String : function(array) {
    return String.fromCharCode.apply(String, array);
  },
  
  stringToBytes : function(s) {
    var data = [];
    for (var i = 0; i < s.length; i++){  
        data.push(s.charCodeAt(i));
    }
    return data;
  },
  
  stringToArrayBuffer : function(string) {
    var buffer = new ArrayBuffer(string.length);
    var bufView = new Uint8Array(buffer);
    for (var i=0; i < string.length; i++) {
        bufView[i] = string.charCodeAt(i);
    }
    return buffer;
  },
  
  arrayBufferToString : function(buffer) {
    var result = "";
  	for(var i = 0; i < buffer.length; ++i){
  		result+= (String.fromCharCode(buffer[i]));
  	}
  	return result;
    //return String.fromCharCode.apply(null, new Uint8Array(buffer));
  },
  
  uint8ArrayToArray : function(uint8Array) {
    var array = [];
    for (var i = 0; i < uint8Array.byteLength; i++) {
        array[i] = uint8Array[i];
    }
    return array;
  },
  
  delay : function(time) {
    var d1 = new Date();
    var d2 = new Date();
    while (d2.valueOf() < d1.valueOf() + time) {
      d2 = new Date();
    }
  },
  
  sleep : function(seconds) {
    
    var start = new Date().getTime();
    
    while (new Date() < start + seconds*1000) {}
    return 0;
  }
};

































