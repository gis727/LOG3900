export const objHasAttrWithAttr: Function = (obj: object, attr: string, target: string): boolean => {
    const keys: string[] = Object.keys(obj);
    for (const key of keys) {
        if (obj[key][attr] !== undefined && obj[key][attr] === target) { return true; }
    }
    return false;
};

// tslint:disable-next-line
export const findIndexOfElemWithAttr: Function = (arr: any[], attr: string, target: string): number => {
    let index: number = -1;
    for (let i: number = 0; i < arr.length; i++) {
        if (arr[i][attr] === target) {
            index = i;
            break;
        }
    }
    return index;
};

export interface GenericObject {
    // tslint:disable-next-line:no-any
    [id: string]: any;
}

// tslint:disable-next-line
export const getRandomIndexInArray: Function = (arr: any[], length?: number): number => {
    return Math.floor(Math.random() * (length ? length : arr.length));
};

// tslint:disable-next-line
export const shuffle: (arr: any[]) => any[] = (array: any[]): any[] => {
    try {
        let currentIndex = array.length, temporaryValue, randomIndex;
        while (0 !== currentIndex) {
            randomIndex = Math.floor(Math.random() * currentIndex);
            currentIndex -= 1;

            temporaryValue = array[currentIndex];
            array[currentIndex] = array[randomIndex];
            array[randomIndex] = temporaryValue;
        }
    } catch {/*...*/ }
    return array;
};