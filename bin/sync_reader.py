#
"""
Simple example on how to send simple reports based on usages
"""
from time import sleep
from msvcrt import kbhit

import pywinusb.hid as hid

def readData(payload):
    print(payload)

    # Parse the x-coordinate
    #mX = payload[1] & 0xFF;
    #mX += (payload[2] & 0xFF) << 8;

    # Parse the y-coordinate.
    #mY = payload[3] & 0xFF;
    #mY += (payload[4] & 0xFF) << 8;

    # Parse the pressure.
    #mPressure = payload[5] & 0xFF;
    #mPressure += (payload[6] & 0xFF) << 8;

    #if(mPressure >= 300):
    #    print(payload)
    #    print('mX: ', mX)
    #    print('mY: ', mY)
    #    print('Pressure: ', mPressure)

    return None

def click_signal(target_usage, target_vendor_id):
    """This function will find a particular target_usage over output reports on
    target_vendor_id related devices, then it will flip the signal to simulate
    a 'click' event"""
    # usually you'll find and open the target device, here we'll browse for the
    # current connected devices
    all_devices = hid.HidDeviceFilter(vendor_id = target_vendor_id).get_devices()
    
    if not all_devices:
        print("Can't find target device (vendor_id = 0x%04x)!" % target_vendor_id)
    else:
        # search for our target usage
        # target pageId, usageId
             
        for device in all_devices:
            try:
                device.open()
                
                # browse output reports, we could search over feature reports also,
                # changing find_output_reports() to find_feature_reports()
                reports = device.find_feature_reports()

                if(reports):
                    report = reports[1]
                    report.get()
                    #print("\nReport found!\n", list(report[target_usage]))
                    #print("All the report: {0}".format(report.get_raw_data()))

                    b_buffer = [0x00]*7
                    b_buffer[0] = 0x04
                    b_buffer[1] = 0x04
                    b_buffer[3] = 0x00

                    #print("\nSending blank buffer", b_buffer)
                    report.set_raw_data(b_buffer)
                    report.send()

                    #buffer[3] = 0x01
                    #print("\nSending clear screen", buffer)
                    #report.set_raw_data(buffer)
                    #report.send()

                    # set it to read data
                    report = reports[2]
                    report.get()
                    #print("\nReport found!\n", list(report[target_usage]))
                    #print("All the report: {0}".format(report.get_raw_data()))

                    buffer = [0x00]*7
                    buffer[0] = 0x05
                    buffer[1] = 0x05
                    buffer[3] = 0x04

                    #print("\nSending read data command", buffer)
                    report.set_raw_data(buffer)
                    report.send()

                    #set custom raw data handler
                    device.set_raw_data_handler(readData)

                    print("\nCONNECTED: Waiting for data ... Press any (system keyboard) key to stop...")
                    while not kbhit() and device.is_plugged():
                        #just keep the device opened to receive events
                        sleep(0.5)
                    return
            finally:
                device.close()

    #
if __name__ == '__main__':
    # 0x2914 used when sync is connected to USB, 0x00F3 when connected over bluetooth
    target_vendor_id = 0x00F3 
    target_usage = hid.get_full_usage_id(0xff00, 0x00) # generic vendor page, usage_id = 2
    # go for it!
    click_signal(target_usage, target_vendor_id)

